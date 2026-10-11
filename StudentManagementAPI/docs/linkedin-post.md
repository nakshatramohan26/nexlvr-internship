# LinkedIn Post Draft

**Before you post:** (1) run the project and the tests on your laptop so every statement below is true for you; (2) push the code to GitHub and replace the placeholders; (3) attach your own Swagger UI screenshot(s). Swagger runs on localhost only, so do not describe it as a hosted link. Delete the "(if true)" notes.

---

## Draft (short)

Excited to share my second project from my Java Development internship at NexLevr! 🚀

I built **StudentManagementAPI**, a REST API using **Java and Spring Boot** to manage student records.

What it includes:
✅ CRUD operations (create, read, update, delete) at `/api/students`
✅ Proper HTTP status codes (201, 200, 204, 400, 404)
✅ Input validation (required fields, email format, year range)
✅ Clear JSON error responses with a global exception handler
✅ Interactive API documentation with **Swagger/OpenAPI**
✅ Automated endpoint tests with Spring Boot Test and MockMvc *(if all tests pass for you)*

Right now the data is stored in memory (a database is the next step), and I ran it locally. Layered design: controller -> service -> data store.

What I learned: REST principles, how Spring Boot structures an application, validation and exception handling, and documenting an API so others can test it easily.

🔗 GitHub: [PASTE YOUR REPOSITORY URL HERE]
📸 Swagger UI screenshot attached.

Thank you to NexLevr for the opportunity and guidance!

#Java #SpringBoot #RESTAPI #Swagger #OpenAPI #BackendDevelopment #Internship #NexLevr #LearningInPublic

---

## Optional shorter version

Built a Spring Boot REST API for student records during my NexLevr Java internship: full CRUD, validation, error handling, and Swagger/OpenAPI docs (running locally). Code: [PASTE YOUR REPOSITORY URL HERE]
#Java #SpringBoot #RESTAPI #Swagger #NexLevr

---

## Notes on honesty

- Do not call it "production-ready" or "deployed"; it is a local learning project.
- Mention the in-memory storage if someone asks, as it is a deliberate first step.
- Only keep the testing bullet if `.\mvnw.cmd test` really passes on your machine.
