# GCP Auth Library

This project provides a library for handling authentication between services using Google Cloud Platform (GCP) metadata. It includes functionality for retrieving and caching authentication tokens, attaching tokens to REST requests, and implementing security features with Spring Security.

## Features

- **Token Management**: Retrieves authentication tokens from GCP metadata and caches them for efficient reuse.
- **REST Interceptor**: Automatically attaches authentication tokens to outgoing REST requests.
- **Spring Security Integration**: Configures Spring Security to handle JWT authentication and authorization.
- **Custom Annotations**: Provides a way to specify required permissions for methods or endpoints using custom annotations.

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven

### Installation

1. Clone the repository:
   ```
   git clone <repository-url>
   ```
2. Navigate to the project directory:
   ```
   cd gcp-auth-library
   ```
3. Build the project using Maven:
   ```
   mvn clean install
   ```

### Usage

To use the library, include it as a dependency in your project. You can then utilize the provided classes to manage authentication and authorization in your application.

### Configuration

Configure the application by editing the `src/main/resources/application.yml` file to set up security settings and GCP metadata endpoint configurations.

### Running Tests

To run the unit tests, execute the following command:
```
mvn test
```

## Contributing

Contributions are welcome! Please submit a pull request or open an issue for any enhancements or bug fixes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.