import API from "./axios";

// Create transaction (Deposit / Withdraw)
export const createTransaction = (data) =>
  API.post("/transactions", data);

// Get all transactions
export const getAllTransactions = () =>
  API.get("/transactions");

// Get by ID
export const getTransactionById = (id) =>
  API.get(`/transactions/${id}`);

// Get transactions by account (Mini Statement)
export const getTransactionsByAccount = (accountId) =>
  API.get(`/transactions/account/${accountId}`);

// Delete (not used in UI ideally)
export const deleteTransaction = (id) =>
  API.delete(`/transactions/${id}`);

export const deleteAllTransactions = () =>
  API.delete("/transactions/reset");
