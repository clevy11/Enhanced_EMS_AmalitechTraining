# Employee Management System

A Java-based Employee Management System (EMS) that provides comprehensive functionality for managing employee records, including CRUD operations, salary management, and performance tracking.

## Features

- **Employee Management**
  - Add, update, and remove employees
  - Auto-incrementing employee IDs
  - Validation for employee details (name, department, salary)
  - Performance rating tracking

- **Salary Management**
  - Set and update employee salaries
  - Apply percentage-based salary raises
  - Salary range filtering
  - Department-wise average salary calculation

- **Search and Filter**
  - Search employees by name
  - Filter by department
  - Filter by salary range
  - Find high-performing employees

- **Sorting**
  - Sort by experience
  - Sort by salary
  - Sort by performance rating

- **Data Validation**
  - Valid salary ranges
  - Valid department names
  - Non-negative performance ratings
  - Proper name validation

## Technical Details

- **Language**: Java
- **Framework**: JavaFX for GUI
- **Testing**: JUnit 5
- **Logging**: Java Logging API
- **Design Patterns**: 
  - Generic programming for flexible ID types
  - Exception handling for robust error management
  - Stream API for efficient data processing

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/
│           └── example/
│               └── employeemanagement/
│                   ├── Employee.java
│                   ├── EmployeeDatabase.java
│                   ├── EmployeeController.java
│                   └── Exception/
│                       ├── EmployeeNotFoundException.java
│                       ├── InvalidDepartmentException.java
│                       └── InvalidSalaryException.java
└── test/
    └── java/
        └── com/
            └── example/
                └── employeemanagement/
                    ├── EmployeeTest.java
                    ├── EmployeeDatabaseTest.java
                    └── EmployeeDebugTest.java
```

## Key Classes

- **Employee**: Represents an employee with attributes like ID, name, department, salary, performance rating, and years of experience.
- **EmployeeDatabase**: Manages employee records with CRUD operations and various search/filter functionalities.
- **EmployeeController**: Handles the GUI interactions and business logic.
- **Exception Classes**: Custom exceptions for handling specific error cases.

## Getting Started

1. **Prerequisites**
   - Java JDK 21 or higher
   - Maven for dependency management
   - JavaFX 23.0.1 or higher

2. **Building the Project**
   ```bash
   mvn clean install
   ```

3. **Running the Application**
   ```bash
   mvn javafx:run
   ```

4. **Running Tests**
   ```bash
   mvn test
   ```

## Testing

The project includes comprehensive test coverage:
- Unit tests for Employee class
- Integration tests for EmployeeDatabase
- Debug tests for specific scenarios
- Test cases for exception handling

## Logging

The system uses Java's built-in logging framework to track:
- Employee creation and updates
- Database operations
- Error conditions
- Performance-related events

## Error Handling

The system implements robust error handling with custom exceptions:
- `EmployeeNotFoundException`: When an employee cannot be found
- `InvalidDepartmentException`: For invalid department names
- `InvalidSalaryException`: For invalid salary values

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- JavaFX for the GUI framework
- JUnit for testing framework
- Maven for build automation 