package com.HiveGroup.HiveRH.Features.Employee;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;
import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;
import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Common.Utils.TextSearchUtils;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import com.HiveGroup.HiveRH.Features.Branch.BranchEntity;
import com.HiveGroup.HiveRH.Features.Branch.BranchRepository;
import com.HiveGroup.HiveRH.Features.Department.DepartamentRepository;
import com.HiveGroup.HiveRH.Features.Department.DepartmentEntity;
import com.HiveGroup.HiveRH.Features.Employee.DTO.*;
import com.HiveGroup.HiveRH.Features.EmployeeAssignment.EmployeeAssignmentDTO;
import com.HiveGroup.HiveRH.Features.EmployeeAssignment.EmployeeAssignmentEntity;
import com.HiveGroup.HiveRH.Features.Position.PositionEntity;
import com.HiveGroup.HiveRH.Features.Position.PositionRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final BranchRepository branchRepository;
    private final AccountRepository accountRepository;
    private final PositionRepository positionRepository;
    private final DepartamentRepository departamentRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public EmployeeResponseDTO create(EmployeeCreateDTO employeeCreateDTO){
        if (employeeCreateDTO.id_branch() == null){
            throw new EntityNotFoundException("La sucursal es obligatoria","Branch");
        }
        if (employeeCreateDTO.id_position() == null){
            throw new EntityNotFoundException("El puesto es obligatorio", "Position");
        }
        if (employeeCreateDTO.id_department() == null){
            throw new EntityNotFoundException("El departamento es obligatorio","Department");
        }

        BranchEntity branch = branchRepository.findById(employeeCreateDTO.id_branch())
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada","Branch"));
        PositionEntity position = positionRepository.findById(employeeCreateDTO.id_position())
                .orElseThrow(() -> new EntityNotFoundException("Puesto no encontrado","Position"));
        DepartmentEntity department = departamentRepository.findById(employeeCreateDTO.id_department())
                .orElseThrow(() -> new EntityNotFoundException("Departamento no encontrado","Department"));

        EmployeeEntity employee = EmployeeEntity.builder()
                .name(employeeCreateDTO.name())
                .lastName(employeeCreateDTO.lastName())
                .phoneNumber(employeeCreateDTO.phoneNumber())
                .genre(employeeCreateDTO.genre())
                .dni(employeeCreateDTO.dni())
                .city(employeeCreateDTO.city())
                .address(employeeCreateDTO.address())
                .birthdate(employeeCreateDTO.birth_date())
                .hireDate(employeeCreateDTO.hire_date())
                .baseSalary(employeeCreateDTO.base_salary())
                .status(StatusEnum.ACTIVE)
                .branch(branch)
                .build();

        EmployeeAssignmentEntity assignment = new EmployeeAssignmentEntity();
        assignment.setEmployee(employee);
        assignment.setPosition(position);
        assignment.setDepartment(department);

        List<EmployeeAssignmentEntity> assignments = new ArrayList<>();
        assignments.add(assignment);
        employee.setAssignments(assignments);

        EmployeeEntity createdEmployee = employeeRepository.save(employee);
        AccountEntity defaultAccount = createDefaultAccount(createdEmployee);
        createdEmployee.setAccount(defaultAccount);
        createdEmployee = employeeRepository.save(createdEmployee);

        return toDTO(createdEmployee);
    }

    @Transactional
    public EmployeeResponseDTO deleteById(Long id){
        EmployeeEntity employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado","Employee"));

        employee.setStatus(StatusEnum.TERMINATED);

        EmployeeEntity deletedEmployee = employeeRepository.save(employee);

        return toDTO(deletedEmployee);
    }

    @Transactional
    public EmployeeResponseDTO putById(Long id, EmployeeUpdateDTO employeeUpdateDTO){

        EmployeeEntity employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado","Employee"));

        employee.setName(employeeUpdateDTO.name());
        employee.setLastName(employeeUpdateDTO.lastName());
        employee.setPhoneNumber(employeeUpdateDTO.phoneNumber());
        employee.setGenre(employeeUpdateDTO.genre());
        employee.setDni(employeeUpdateDTO.dni());
        employee.setCity(employeeUpdateDTO.city());
        employee.setAddress(employeeUpdateDTO.address());
        employee.setBirthdate(employeeUpdateDTO.birth_date());
        employee.setHireDate(employeeUpdateDTO.hire_date());
        employee.setTerminationDate(employeeUpdateDTO.termination_date());
        employee.setStatus(employeeUpdateDTO.status());
        employee.setBaseSalary(employeeUpdateDTO.base_salary());
        BranchEntity branch = branchRepository.findById(employeeUpdateDTO.id_branch())
                .orElseThrow(()->new EntityNotFoundException("Sucursal no encontrada","Branch"));
        employee.setBranch(branch);

        if (employeeUpdateDTO.id_account() != null){
            AccountEntity account = accountRepository.findById(employeeUpdateDTO.id_account())
                    .orElseThrow(() -> new EntityNotFoundException("Cuenta no encotrada","Account"));
            employee.setAccount(account);
        }else {
            employee.setAccount(null);
        }

        EmployeeEntity updatedEmployee = employeeRepository.save(employee);

        return toDTO(updatedEmployee);
    }


    @Transactional
    public EmployeeResponseDTO patchById(Long id, EmployeeUpdateDTO employeeUpdateDTO){

        EmployeeEntity employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado", "Employee"));

        employee.setName(employeeUpdateDTO.name() != null ? employeeUpdateDTO.name() : employee.getName());
        employee.setLastName(employeeUpdateDTO.lastName() != null ? employeeUpdateDTO.lastName() : employee.getLastName());
        employee.setPhoneNumber(employeeUpdateDTO.phoneNumber() != null ? employeeUpdateDTO.phoneNumber() : employee.getPhoneNumber());
        employee.setGenre(employeeUpdateDTO.genre() != null ? employeeUpdateDTO.genre() : employee.getGenre());
        employee.setDni(employeeUpdateDTO.dni() != null ? employeeUpdateDTO.dni() : employee.getDni());
        employee.setCity(employeeUpdateDTO.city() != null ? employeeUpdateDTO.city() : employee.getCity());
        employee.setAddress(employeeUpdateDTO.address() != null ? employeeUpdateDTO.address() : employee.getAddress());
        employee.setBirthdate(employeeUpdateDTO.birth_date() != null ? employeeUpdateDTO.birth_date() : employee.getBirthdate());
        employee.setHireDate(employeeUpdateDTO.hire_date() != null ? employeeUpdateDTO.hire_date() : employee.getHireDate());
        employee.setTerminationDate(employeeUpdateDTO.termination_date() != null ? employeeUpdateDTO.termination_date() : employee.getTerminationDate());
        employee.setStatus(employeeUpdateDTO.status() != null ? employeeUpdateDTO.status() : employee.getStatus());
        employee.setBaseSalary(employeeUpdateDTO.base_salary() != null ? employeeUpdateDTO.base_salary() : employee.getBaseSalary());


        if (employeeUpdateDTO.id_branch() != null){
            BranchEntity branch = branchRepository.findById(employeeUpdateDTO.id_branch())
                    .orElseThrow(()->new EntityNotFoundException("Sucursal no encontrada", "Branch"));

            employee.setBranch(branch);
        }

        if (employeeUpdateDTO.id_account() != null){
            AccountEntity account = accountRepository.findById(employeeUpdateDTO.id_account())
                    .orElseThrow(() -> new EntityNotFoundException("Cuenta no encotrada", "Account"));
            employee.setAccount(account);
        }
        EmployeeEntity updatedEmployee = employeeRepository.save(employee);

        return toDTO(updatedEmployee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDTO findById(Long id){
        EmployeeEntity employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado", "Employee"));
        return toDTO(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDTO findCurrentEmployee() {
        AccountEntity account = getCurrentAccount();
        if (account.getEmployee() == null) {
            throw new EntityNotFoundException("Empleado no encontrado para la cuenta autenticada", "Employee");
        }

        return toDTO(account.getEmployee());
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> findAllbyFilter(EmployeeFilterDTO filters){
        EmployeeFilterDTO activeFilters = filters != null
                ? filters
                : new EmployeeFilterDTO(null, null, null, null, null, null, null, null, null, null);
        List<EmployeeEntity> employeeList = employeeRepository.findAll();


        return employeeList.stream()
                .map(this::toDTO)
                .filter(employee -> TextSearchUtils.matchesFullName(employee.name(), employee.lastName(), activeFilters.fullName()))
                .filter(employee -> activeFilters.dni() == null || employee.dni().equals(activeFilters.dni()))
                .filter(employee -> activeFilters.id_branch() == null || employee.branch_id().equals(activeFilters.id_branch()))
                .filter(employee -> activeFilters.hire_date() == null || employee.hireDate().equals(activeFilters.hire_date()))
                .filter(employee -> activeFilters.termination_date() == null || employee.terminationDate().equals(activeFilters.termination_date()))
                .filter(employee -> activeFilters.status() == null || employee.status() == activeFilters.status())
                .filter(employee -> activeFilters.position() == null || employee.assignments().stream().anyMatch(a -> a.positionName().equalsIgnoreCase(activeFilters.position())))
                .filter(employee -> activeFilters.department() == null || employee.assignments().stream().anyMatch(a -> a.departmentName().equalsIgnoreCase(activeFilters.department())))
                .filter(employee -> activeFilters.min_salary() == null || employee.baseSalary() >= activeFilters.min_salary())
                .filter(employee -> activeFilters.max_salary() == null || employee.baseSalary() <= activeFilters.max_salary())
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<EmployeeResponseDTO> getAllPage(Pageable pageable){
        Page<EmployeeEntity> page = employeeRepository.findAll(pageable);

        return new PageResponseDTO<>(
                page.getContent()
                        .stream()
                        .map(this::toDTO)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private EmployeeResponseDTO toDTO(EmployeeEntity employee){
        List<EmployeeAssignmentDTO> assignments = employee.getAssignments() == null
                ? List.of()
                : employee.getAssignments().stream()
                        .map(assignment -> new EmployeeAssignmentDTO(
                                assignment.getDepartment().getId_department(),
                                assignment.getDepartment().getDepartmentName(),
                                assignment.getPosition().getId_position(),
                                assignment.getPosition().getPositionName()
                        )).toList();

        return new EmployeeResponseDTO(
            employee.getId_employee(),
                employee.getName(),
                employee.getLastName(),
                employee.getPhoneNumber(),
                employee.getGenre(),
                employee.getDni(),
                employee.getCity(),
                employee.getAddress(),
                employee.getBirthdate(),
                employee.getHireDate(),
                employee.getTerminationDate(),
                employee.getBaseSalary(),
                employee.getStatus(),
                employee.getBranch().getId_branch(),
                employee.getAccount() != null ? employee.getAccount().getId_account() : null,
                assignments
        );
    }

    private AccountEntity createDefaultAccount(EmployeeEntity employee) {
        String username = "user_" + employee.getId_employee();
        AccountEntity account = AccountEntity.builder()
                .user(username)
                .email(username + "@hiverh.local")
                .password(passwordEncoder.encode("123"))
                .rol(RolEnum.EMPLOYEE)
                .statusEnum(StatusEnum.ACTIVE)
                .build();

        return accountRepository.save(account);
    }

    private AccountEntity getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AccessDeniedException("No hay usuario autenticado");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof AccountEntity account) {
            return account;
        }

        String username = authentication.getName();
        return accountRepository.findByUserOrEmail(username, username)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta inexistente", "AccountEntity"));
    }

}
