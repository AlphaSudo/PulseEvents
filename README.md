# Event Management Frontend

## Overview

This project is a frontend application for browsing and managing events. Users can view a list of available events, see detailed information for each event, and book events. The application features a responsive design with light and dark mode support.

## Frontend

### Technology Stack

*   **React:** A JavaScript library for building user interfaces.
*   **Vite:** A fast build tool and development server for modern web projects.
*   **TypeScript:** A superset of JavaScript that adds static typing.
*   **Tailwind CSS:** A utility-first CSS framework for rapid UI development.
*   **React Router:** For declarative routing in the React application.

### Project Structure

The project follows a standard structure for React applications:

```
/
├── public/             # Static assets
├── src/                # Source files
│   ├── assets/         # Images, fonts, etc.
│   ├── components/     # Reusable UI components (e.g., TopNavigationBar, EventList, EventCard)
│   ├── contexts/       # React Context API for state management (e.g., BookingContext)
│   ├── pages/          # Page-level components (e.g., EventListPage, EventDetailPage)
│   ├── styles/         # Global styles, Tailwind CSS configuration (e.g., index.css)
│   ├── App.tsx         # Main application component with routing setup
│   └── main.tsx        # Entry point of the application
├── .eslintrc.cjs       # ESLint configuration
├── .gitignore          # Git ignore file
├── index.html          # HTML entry point
├── package.json        # Project metadata and dependencies
├── postcss.config.js   # PostCSS configuration
├── README.md           # This file
├── tailwind.config.js  # Tailwind CSS configuration
└── tsconfig.json       # TypeScript configuration
└── tsconfig.node.json  # TypeScript configuration for Node.js environment (e.g. Vite config)
```

### Key Features & Components

*   **Event Listing:** Displays a list of events, typically with filtering and sorting capabilities.
    *   `EventList.tsx`: Component responsible for fetching and displaying the list of events.
    *   `EventCard.tsx`: Component for displaying individual event summaries.
*   **Event Detail Page:** Shows comprehensive information about a selected event.
    *   `EventDetailPage.tsx`: Component that renders event details, including description, date, location, price, and an image.
*   **Booking System:** Allows users to book events.
    *   `BookingContext.tsx`: Manages the state of event bookings across the application.
    *   Booking functionality is integrated into `EventDetailPage.tsx`.
*   **Navigation:**
    *   `TopNavigationBar.tsx`: Provides consistent navigation across the application.
*   **Theming:**
    *   Supports light and dark modes, configured via Tailwind CSS.
    *   Global styles are managed in `src/index.css` (or a similar file).
*   **Responsive Design:** The UI is designed to adapt to various screen sizes using Tailwind CSS utility classes.

## Getting Started

### Prerequisites

*   Node.js (v18.x or later recommended)
*   npm (v9.x or later) or yarn (v1.22.x or later)

### Installation

1.  Clone the repository:
    ```bash
    git clone <repository-url>
    ```
2.  Navigate to the project directory:
    ```bash
    cd vite-react-tailwind-starter-master # Or your project directory name
    ```
3.  Install dependencies:
    ```bash
    npm install
    ```
    or
    ```bash
    yarn install
    ```

### Running the Development Server

To start the development server, run:

```bash
npm run dev
```

or

```bash
yarn dev
```

This will typically start the application on `http://localhost:5173`.

### Building for Production

To create a production build, run:

```bash
npm run build
```

or

```bash
yarn build
```

The production-ready files will be generated in the `dist` directory.

## Expanding the ESLint configuration

If you are developing a production application, we recommend updating the configuration to enable type aware lint rules:

- Configure the top-level `parserOptions` property like this:

```js
   parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module',
    project: ['./tsconfig.json', './tsconfig.node.json'],
    tsconfigRootDir: __dirname,
   },
```

- Replace `plugin:@typescript-eslint/recommended` to `plugin:@typescript-eslint/recommended-type-checked` or `plugin:@typescript-eslint/strict-type-checked`
- Optionally add `plugin:@typescript-eslint/stylistic-type-checked`
- Install [eslint-plugin-react](https://github.com/jsx-eslint/eslint-plugin-react) and add `plugin:react/recommended` & `plugin:react/jsx-runtime` to the `extends` list

## License 📄

[MIT License](https://github.com/moinulmoin/vite-react-tailwind-starter/blob/master/LICENSE)

## Backend (Authentication Service)

### Overview
This module provides a simple authentication service built with Spring Boot, Spring Data JPA and Jakarta Persistence. It manages user registration, credential storage (BCrypt), and role-based access.

### Tech Stack
- Java 17
- Spring Boot
- Spring Data JPA
- Jakarta Persistence (Jakarta EE)
- PostgreSQL
- Maven (or Gradle)

### Running the Service
1. Ensure PostgreSQL is running and the database/schema exist.
2. Update credentials in `application.yml`.
3. Build and run:
   ```bash
   mvn clean package
   java -jar target/authentication-service.jar
   ```
4. Verify the tables (`users`, `user_roles`) are created automatically.

### Common Endpoints
_(Example endpoints – adjust to your controllers)_
- `POST /api/auth/register`  
- `POST /api/auth/login`  

### License & Contribution
Feel free to fork and send pull requests. Make sure to write tests for any new features.

