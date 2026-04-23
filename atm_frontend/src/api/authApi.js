import axios from "./axios";

// Login by account number and PIN
export const login = async (accountNumber, pin) => {
  const res = await axios.post("/auth/login", { accountNumber, pin });
  const { token, refreshToken } = res.data;
  localStorage.setItem("token", token);
  localStorage.setItem("refreshToken", refreshToken);
  return res.data;
};

// Logout
export const logout = async () => {
  await axios.post("/auth/logout");
  localStorage.removeItem("token");
  localStorage.removeItem("refreshToken");
};

// Check if user is authenticated
export const isAuthenticated = () => {
  return !!localStorage.getItem("token");
};
