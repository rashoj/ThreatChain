import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

const ThreatDetail = () => {
  const { id } = useParams();
  const [threat, setThreat] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchThreat = async () => {
      try {
        const res = await fetch(`http://localhost:8081/api/summaries/${id}`, {
          headers: {
            'x-api-key': 'e3a490ef03b1bb5a89cb9b57cf5087dc5794b8e9774e1a8bda6540ed59932650'
          }
        });

    
        if (!res.ok) {
          throw new Error('Threat not found');
        }

        const data = await res.json();
        setThreat(data);
      } catch (err) {
        setError(err.message);
      }
    };

    fetchThreat();
  }, [id]);

  if (error) return <p className="text-danger mt-5">Error: {error}</p>;
  if (!threat) return <p className="mt-5">Loading...</p>;

  return (
    <div className="container mt-5">
      <h2>{threat.title}</h2>
      <p><strong>Category:</strong> {threat.category}</p>
      <p><strong>Sectors:</strong> {threat.sectors}</p>
      <p><strong>Countries:</strong> {threat.countries}</p>
      <p><strong>Risk Score:</strong> {threat.risk_score}</p>
      <div className="mt-3">
        <strong>🧠 AI Summary:</strong>
        <p>{threat.ai_summary}</p>
      </div>
    </div>
  );
};

export default ThreatDetail;
