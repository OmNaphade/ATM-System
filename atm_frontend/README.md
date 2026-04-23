# ATM Frontend Application

A modern React-based frontend application for the ATM (Automated Teller Machine) system, providing a user-friendly interface for account management and transactions.

## Features

- **User Authentication**: Secure login with account number and PIN
- **Dashboard**: Overview of account information and recent transactions
- **Deposit**: Add funds to your account
- **Withdraw**: Withdraw funds from your account
- **Profile Management**: View and update user profile information
- **Responsive Design**: Mobile-friendly interface using Tailwind CSS
- **Real-time Updates**: Automatic token refresh and session management

## Technology Stack

- **React 18**: Modern JavaScript library for building user interfaces
- **React Router**: Declarative routing for React applications
- **Axios**: HTTP client for API communication with interceptors for JWT handling
- **Tailwind CSS**: Utility-first CSS framework for styling
- **Create React App**: Build setup and development server

## Getting Started

### Prerequisites

- Node.js 16+
- npm or yarn
- Backend server running on `http://localhost:8080`

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd atm_frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start the development server**
   ```bash
   npm start
   ```

   The application will open at [http://localhost:3000](http://localhost:3000)

### Available Scripts

- `npm start` - Runs the app in development mode
- `npm test` - Launches the test runner
- `npm run build` - Builds the app for production
- `npm run eject` - Ejects from Create React App (irreversible)

## Project Structure

```
src/
├── api/
│   ├── authApi.js      # Authentication API calls
│   ├── accountApi.js   # Account management API calls
│   ├── transactionApi.js # Transaction API calls
│   └── axios.js        # Axios configuration with interceptors
├── components/
│   ├── Navbar.jsx      # Navigation bar
│   ├── Sidebar.jsx     # Sidebar navigation
│   └── Footer.jsx      # Footer component
├── layouts/
│   └── MainLayout.jsx  # Main application layout
├── pages/
│   ├── Login.jsx       # Login page
│   ├── Dashboard.jsx   # Dashboard page
│   ├── Deposit.jsx     # Deposit page
│   ├── Withdraw.jsx    # Withdraw page
│   └── Profile.jsx     # Profile page
├── App.jsx             # Main application component
├── index.js            # Application entry point
└── index.css           # Global styles
```

## API Integration

The frontend communicates with the backend API at `http://localhost:8080/api`. Key features include:

- **JWT Token Management**: Automatic token storage and refresh
- **Error Handling**: Centralized error handling for API responses
- **Loading States**: User feedback during API calls
- **Authentication Guards**: Protected routes requiring authentication

## Security Features

- **Secure Authentication**: JWT-based login with automatic token refresh
- **Session Persistence**: Users remain logged in across browser sessions
- **Secure API Calls**: All requests include proper authorization headers
- **Input Validation**: Client-side validation for forms and inputs

## Deployment

### Production Build

```bash
npm run build
```

This creates a `build` folder with optimized production files.

### Serving the Build

The build can be served using any static file server:

```bash
npm install -g serve
serve -s build
```

### Docker Deployment

```dockerfile
FROM nginx:alpine
COPY build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## Environment Configuration

Create a `.env` file in the root directory:

```env
REACT_APP_API_BASE_URL=http://localhost:8080/api
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License.
