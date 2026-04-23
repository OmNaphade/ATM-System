import { useEffect, useState } from "react";
import { getAllTransactions } from "../api/transactionApi";

export default function Dashboard() {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchTransactions = async () => {
      setLoading(true);
      try {
        const res = await getAllTransactions();
        setTransactions(res.data);
      } catch (err) {
        console.error(err);
      }
      setLoading(false);
    };

    fetchTransactions();
  }, []);

  return (
    <div className="card max-w-5xl mx-auto mt-10 p-6">
      <h2 className="text-2xl font-bold mb-2">Dashboard</h2>

      {loading ? (
        <p>Loading...</p>
      ) : (
        <table className="table w-full">
          <thead>
            <tr>
              <th>ID</th>
              <th>Type</th>
              <th>Amount</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            {transactions.map((txn) => (
              <tr key={txn.transactionId}>
                <td>{txn.transactionId}</td>
                <td>{txn.type}</td>
                <td>₹{txn.amount}</td>
                <td>
                  {txn.timestamp
                    ? new Date(txn.timestamp).toLocaleString()
                    : "N/A"}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
