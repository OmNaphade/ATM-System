import API from "./axios";

export const getAllAccounts = () => API.get("/accounts");

export const getAccountById = (id) => API.get(`/accounts/${id}`);

export const createAccount = (data) => API.post("/accounts", data);

export const updateAccount = (id, data) => API.put(`/accounts/${id}`, data);

export const deleteAccount = (id) => API.delete(`/accounts/${id}`);

export const deleteAllAccounts = () => API.delete("/accounts/reset");
