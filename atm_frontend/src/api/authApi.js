import axios from "./axios";

// Login by account number (e.g., SBIN10001) and PIN
export const login = async (accountNumber, pin) => {
  const res = await axios.get(`/accounts/number/${accountNumber}`);

  // Check PIN
  if (res.data && res.data.pin === pin) {
    return res.data;
  } else {
    throw new Error("Invalid credentials");
  }
};
