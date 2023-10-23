<img src="./docs-imgs/addbazaar_logo.jpg" align="center" alt="Team Challenge. AdBazaar project">

## 1. About the project

## 2. Front-end part of the project

Here is the front-end part of our project: <b>https://github.com/toryrory/adbazaar-frontend </b>

## 3. How to start a project locally

### 3.1 Prerequisites

* Java 17 or higher
* PostgreSQL 16

### 3.2 How to run

1. `Clone dev` branch.
2. Open with your preferable IDE(IDEA, VScode).
3. Open `Terminal` type `git checkout -b <branch_name>` or use git GUI plugins (this will create a new local branch).
4. Create a database at your Postgres server with the name `adbazaar`.
5. `Add/Edit Configuration environment variables`:

    ![env-vars](./docs-imgs/env_vars.jpg)

6. Change `spring.profiles.active` to `default` from `dev_qa` in `application.yml`.
7. `Run Application`.
8. If you did everything correctly, you should be able to access Swagger by this URL: <b>http://localhost:8080/swagger-ui/index.html </b>
