# Course Registration System

A backend REST API built with Java and Spring Boot for managing university course registrations. The system supports three roles Student, Professor and Admin each with their own set of features and access controls secured using JWT authentication.

---
## Features

### Student
- View personal profile including CGPA and credit information
- Browse all eligible courses based on CGPA and seat availability
- Register for courses with concurrent enrollment protection
- Drop elective courses (core courses cannot be dropped)
- View grades by semester or across all semesters

### Professor
- View all courses assigned to them
- View list of actively enrolled students in their course
- Assign and update grades for students
- Update minimum CGPA requirement for their courses
- CGPA is automatically recalculated after every grade assignment

### Admin
- Add new courses with full configuration
- Update seat matrix, credit hours and core course status
- Assign or reassign professors to courses
- View all students and courses in the system


## Security

- Passwords encrypted using **BCrypt**
- All protected endpoints require a valid **JWT token**
- Tokens expire after **24 hours**
- Role based access control restricts endpoints by user role
- Professor can only manage their own courses
- Student can only access their own enrollments and grades
- Concurrent course registration handled using **JPA Optimistic Locking**

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java |
| Framework | Spring Boot, Spring MVC, Spring Security |
| Authentication | JWT (JSON Web Token) |
| Database | PostgreSQL |
| ORM | Hibernate, Spring Data JPA |
| Build Tool | Maven |
| Password Encryption | BCrypt |

---

## Getting Started

### Prerequisites

- Java 17+
- PostgreSQL
- Maven

### Setup

**1. Clone the repository**
```bash
git clone https://github.com/yourusername/course-registration-system.git
cd course-registration-system
```

**2. Configure database in `application.properties`**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/courseregistration
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

**3. Configure JWT**
```properties
jwt.secret=your_secret_key_here
jwt.expiration=86400000
```

**4. Run the application**
```bash
mvn spring-boot:run
```

Application starts at `http://localhost:8080`

---

## Authentication

This API uses JWT Bearer token authentication.

**How to authenticate:**
1. Login via the public login endpoint
2. Copy the token from the response
3. Add it to the `Authorization` header of all subsequent requests

```
Authorization: Bearer <your_token_here>
```

---

## API Endpoints

### Authentication (Public)

| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/auth/login` | Login and get JWT token | Public |

---

### Admin APIs

| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/admin/courses` | Add a new course | Admin |
| GET | `/admin/courses` | View all courses | Admin |
| GET | `/admin/students` | View all students | Admin |
| PUT | `/admin/courses/{code}/seats` | Update seat matrix | Admin |
| PUT | `/admin/courses/{code}/professor` | Assign or change professor | Admin |
| PUT | `/admin/courses/{code}/core-status` | Update core course status | Admin |
| PUT | `/admin/courses/{code}/credit-hours` | Update credit hours | Admin |

---

### Professor APIs

| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/professor/courses` | View assigned courses | Professor |
| GET | `/professor/courses/{courseId}/students` | View enrolled students | Professor |
| POST | `/professor/grades` | Assign grade to a student | Professor |
| PUT | `/professor/courses/{courseId}/cgpa-criteria` | Update minimum CGPA requirement | Professor |

---

### Student APIs

| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/student/profile` | View own profile and credit info | Student |
| GET | `/student/courses/eligible` | View all eligible courses | Student |
| POST | `/student/enroll/{courseCode}` | Enroll in a course | Student |
| GET | `/student/enrollments` | View registered courses | Student |
| DELETE | `/student/drop/{courseCode}` | Drop a course | Student |
| GET | `/student/grades` | View all grades | Student |
| GET | `/student/grades/{semester}` | View grades by semester | Student |

---

## Request Examples

### Login
```
POST /auth/login
Content-Type: application/json

{
    "username": "student@gmail.com",
    "password": "password123"
}
```

Response:
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "role": "STUDENT"
}
```

### Add Course (Admin)
```
POST /admin/courses
Authorization: Bearer <token>
Content-Type: application/json

{
    "name": "Data Structures",
    "code": "CS301",
    "totalSeats": 50,
    "creditHours": 3,
    "isCoreFlag": true,
    "minCgpaRequired": 2.5,
    "professorEmail": "200217344@edu.ac.in"
}
```

### Enroll in Course (Student)
```
POST /student/enroll/CS301
Authorization: Bearer <token>
```

### Grade a Student (Professor)
```
POST /professor/grades
Authorization: Bearer <token>
Content-Type: application/json

{
    "courseCode": "CS503",
    "studentId": "2024030019",
    "semester": "IV"
    "letterGrade": "A",
}
```

---



---

## Project Structure
```
Course-Registration-System/
│
└── 📁 src/
    └── 📁 main/
        └── 📁 java/
            └── 📦 com.university.courseRegistrationSystem/
                │
                ├── 📁 config/
                │   ├── ApplicationConfig.java      # App-level beans & config
                │   └── SecurityConfig.java         # Spring Security setup
                │
                ├── 📁 controller/
                │   ├── AdminController.java        # Admin APIs (courses, users)
                │   ├── AuthController.java         # Login / Register
                │   ├── ProfessorController.java    # Professor APIs
                │   └── StudentController.java      # Student APIs
                │
                ├── 📁 dto/
                │   ├── AuthResponse.java
                │   ├── CourseRequest.java
                │   ├── CourseResponse.java
                │   ├── DropCourseRequest.java
                │   ├── EnrollmentRequest.java
                │   ├── EnrollmentResponse.java
                │   ├── GradeRequest.java
                │   ├── GradeResponse.java
                │   ├── LoginRequest.java
                │   ├── StudentEnrollmentResponse.java
                │   ├── StudentProfileResponse.java
                │   └── StudentSummaryResponse.java
                │
                ├── 📁 exception/
                │   ├── AlreadyEnrolledException.java
                │   ├── CannotDropCoreException.java
                │   ├── CourseFullException.java
                │   ├── CourseNotFoundException.java
                │   ├── EnrollmentNotFoundException.java
                │   ├── GradeAlreadyExistsException.java
                │   ├── NotEligibleException.java
                │   ├── NotEnrolledException.java
                │   ├── ProfessorNotAuthorizedException.java
                │   └── StudentNotFoundException.java
                │
                ├── 📁 model/
                │   ├── Admin.java
                │   ├── Course.java
                │   ├── Enrollment.java
                │   ├── EnrollmentStatus.java       # Enum
                │   ├── Grade.java
                │   ├── LetterGrade.java            # Enum
                │   ├── Professor.java
                │   ├── Role.java                   # Enum
                │   ├── Student.java
                │   └── User.java
                │
                ├── 📁 repository/
                │   ├── AdminRepository.java
                │   ├── CourseRepository.java
                │   ├── EnrollmentRepository.java
                │   ├── GradeRepository.java
                │   ├── ProfessorRepository.java
                │   ├── StudentRepository.java
                │   └── UserRepository.java
                │
                ├── 📁 security/
                │   ├── JWTFilter.java              # JWT request filter
                │   └── JWTService.java             # Token generate/validate
                │
                └── 📁 service/
                    ├── AdminService.java
                    ├── AuthService.java
                    ├── CustomUserDetailsService.java  # Spring Security user loader
                    ├── EnrollmentService.java
                    ├── GradeService.java
                    ├── ProfessorService.java
                    └── StudentService.java
```
 
---
