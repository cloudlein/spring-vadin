# HTTP Client API Testing Guide

This directory contains `.http` API testing suites configured for the `spring-vadin` application. These files follow the standard HTTP Client / REST Client format supported by multiple editors and command-line tools.

## Supported Clients & Tools

- **JetBrains IDEs** (IntelliJ IDEA, WebStorm, Fleet, etc.) - Built-in HTTP Client
- **VS Code / VSCodium** - Extensions like [REST Client](https://marketplace.visualstudio.com/items?itemName=humao.rest-client) or [Huachao Mao REST Client](https://marketplace.visualstudio.com/items?itemName=humao.rest-client) / [HTTP Client](https://marketplace.visualstudio.com/items?itemName=mkloubert.vscode-http-client)
- **CLI / CI/CD** - [JetBrains HTTP Client CLI](https://www.jetbrains.com/help/idea/http-client-cli.html) or `httpyac`

## Directory Structure

```text
http-requests/
├── http-client.env.json                 # Environment configurations (local, dev) and variables
├── auth.http                            # Authentication API tests (Register, Login, Logout, Validations)
├── user.http                            # User Management API tests (CRUD, Pagination, Unauthorized access)
├── role.http                            # Role Management API tests (CRUD, Pagination)
├── Spring_Vadin_API.postman_collection.json # Postman Collection v2.1 (Full API + Scripts + Tests)
├── Spring_Vadin_Local.postman_environment.json # Postman Environment (Localhost)
└── README.md                            # Usage documentation
```

---

## Execution Guide

1. **Open an HTTP File**:
   - Open any `.http` file (e.g. `auth.http`, `user.http`) in your editor or IDE.

2. **Select Environment**:
   - Select the target environment (e.g., `local` or `dev` as configured in `http-client.env.json`).
   - In JetBrains IDEs: choose the environment from the top-right "Run with" dropdown.
   - In VS Code (REST Client): select environment from the status bar or configure matching environment settings.
   - In CLI: pass `--env=local` (e.g. `ijhttp --env=local auth.http`).

3. **Run Requests**:
   - Trigger requests using the editor's gutter run button (e.g., `Send Request` / green play icon) or via CLI commands.

4. **Automated Token Management**:
   - After executing a successful **Login** request, the response handler extracts the JWT token and persists it to a global variable:
     ```javascript
     client.global.set("auth_token", response.body.data.token);
     ```
   - Subsequent authenticated requests that specify `Authorization: Bearer {{auth_token}}` will reuse this token automatically.

5. **Private Credentials & Variables (Optional)**:
   - For sensitive credentials or secrets that should not be committed to Git, use `http-client.private.env.json`. It will override/supplement public environment variables and is excluded by `.gitignore`.

