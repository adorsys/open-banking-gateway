# Fintech UI

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 8.3.20.

## Development server

Run `npm run start` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

### Running with docker-compose environment

If you want to use the backend, that is start from docker-compose, use `npm run serve:docker`. By that `http://localhost:18086` as backend url will be used.

### Running against dev environment

For quick frontend only development, you can develop against hosted development environment. Use `npm run serve:dev`. By that `https://obg-dev-fintechui.cloud.adorsys.de/fintech-api-proxy` as backend url will be used.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `npm run build` to build the project. The build artifacts will be stored in the `dist/` directory. Use `npm run build:prod` for a production build.

## Running unit tests

Run `npm run test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `npm run e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).

## Running formatting

If you execute `npm install` a pre-commit hook will be installed. We use Prettier as formatter. If you want to reformat all files in project, use `npm run format:fix`.
