import { useEffect, useState } from "react";
import { getAccountById } from "../api/accountApi";

export default function Profile() {
  const [account, setAccount] = useState(null);
  const accountId = localStorage.getItem("accountId");

  useEffect(() => {
    const fetchAccount = async () => {
      try {
        const res = await getAccountById(accountId);
        setAccount(res.data);
      } catch (err) {
        console.error(err);
      }
    };

    fetchAccount();
  }, [accountId]);

  return (
    <div className="card max-w-md mx-auto mt-10">
      <h2 className="text-2xl font-bold text-center mb-4">
        Profile
      </h2>

      {account && (
        <div className="space-y-3">
          <p>Account ID: {account.accountId}</p>
          <p>Balance: ₹{account.balance}</p>
          <p>Type: {account.accountType}</p>
          <p>Bank: {account.bankName}</p>
        </div>
      )}
    </div>
  );
}
