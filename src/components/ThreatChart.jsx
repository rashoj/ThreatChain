import React from 'react';
import { Pie, Bar } from 'react-chartjs-2';
import {
  Chart,
  BarElement,
  CategoryScale,
  LinearScale,
  ArcElement,
  Tooltip,
  Legend,
} from 'chart.js';

Chart.register(BarElement, ArcElement, CategoryScale, LinearScale, Tooltip, Legend);

const ThreatCharts = ({ threats }) => {
  if (!Array.isArray(threats) || threats.length === 0) {
    return <p className="text-muted">No threat data available for charting.</p>;
  }

  // === Pie Chart (Risk Distribution) ===
  const riskCounts = { high: 0, medium: 0, low: 0 };
  threats.forEach(threat => {
    const risk = (threat.risk_score || '').toLowerCase();
    if (['high', 'medium', 'low'].includes(risk)) {
      riskCounts[risk]++;
    }
  });

  const pieData = {
    labels: ['High', 'Medium', 'Low'],
    datasets: [
      {
        data: [riskCounts.high, riskCounts.medium, riskCounts.low],
        backgroundColor: ['#dc3545', '#ffc107', '#28a745'],
      },
    ],
  };

  // === Bar Chart (Top Malware Families) ===
  const malwareMap = {};
  threats.forEach(threat => {
    const malware = threat.malware_family || 'Unknown';
    malwareMap[malware] = (malwareMap[malware] || 0) + 1;
  });

  const sortedMalware = Object.entries(malwareMap)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 5);

  const barData = {
    labels: sortedMalware.map(([name]) => name),
    datasets: [
      {
        label: 'Top Malware Families',
        data: sortedMalware.map(([, count]) => count),
        backgroundColor: 'rgba(54, 162, 235, 0.6)',
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'top' },
      tooltip: { mode: 'index', intersect: false },
    },
    scales: {
      x: { title: { display: true } },
      y: { title: { display: true }, beginAtZero: true },
    },
  };

  return (
    <div className="row mt-5">
      {/* Malware Bar Chart */}
      <div className="col-md-6" style={{ height: '300px' }}>
        <h6 className="text-center">Top Malware Families</h6>
        <Bar data={barData} options={chartOptions} />
      </div>

      {/* Pie Chart */}
      <div className="col-md-6" style={{ height: '300px' }}>
        <h6 className="text-center">Risk Distribution</h6>
        <Pie
          data={pieData}
          options={{
            responsive: true,
            plugins: { legend: { position: 'right' } },
          }}
        />
      </div>
    </div>
  );
};

export default ThreatCharts;
