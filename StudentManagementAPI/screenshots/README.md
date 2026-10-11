# Screenshots

This folder is for **your own real screenshots**, taken while running the project on your laptop. No screenshots are included, and none should be edited or invented. Save them as PNG with these names.

| # | File name | What it must show |
|---|---|---|
| 1 | `01-app-running-terminal.png` | VS Code terminal with `.\mvnw.cmd spring-boot:run` and the "Started StudentManagementApiApplication" / "Tomcat started on port 8080" lines |
| 2 | `02-swagger-ui-all-endpoints.png` | Swagger UI at `/swagger-ui/index.html` with all five endpoints visible (browser address bar included) |
| 3 | `03-create-student-201.png` | `POST /api/students` expanded: request body, **Server response 201**, response body and `Location` header |
| 4 | `04-get-student-200.png` | `GET /api/students` and/or `GET /api/students/{id}` returning **200** with the student |
| 5 | `05-update-student-200.png` | `PUT /api/students/{id}` returning **200** with the changed data |
| 6 | `06-delete-student-204.png` | `DELETE /api/students/{id}` returning **204** (and ideally a following GET giving 404) |
| 7 | `07-validation-error-400.png` | `POST` with invalid data returning **400** and the `fieldErrors` body |
| 8 | `08-automated-tests-output.png` | Terminal after `.\mvnw.cmd test` showing the `Tests run: ...` line and `BUILD SUCCESS` (only if that is what you really get) |

Optional extras: `09-not-found-404.png` (GET with a missing id), `10-openapi-json.png` (`/v3/api-docs` in the browser), `11-project-structure.png` (VS Code Explorer), `12-github-repo.png` (your pushed repository), `13-architecture-diagram.png` (`docs/architecture.png`).

Tips: use `Win + Shift + S` to capture; keep the browser address bar and terminal prompt visible; zoom in so the status codes are readable; do not show personal data other than the sample values.
