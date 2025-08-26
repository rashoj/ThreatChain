import React from 'react';
import { Line } from 'react-chartjs-2';
import {
  Chart,
  LineElement,
  CategoryScale,
  LinearScale,
  PointElement,
  Tooltip,
  Legend,
} from 'chart.js';

Chart.register(LineElement, CategoryScale, LinearScale, PointElement, Tooltip, Legend);

const ThreatTrendsChart = ({ threats }) => {
  const dateCounts = {};

  threats.forEach((threat) => {
    let dateStr = '';
    if (threat.generated_at && Array.isArray(threat.generated_at)) {
      const [year, month, day] = threat.generated_at;
      const dateObj = new Date(year, month - 1, day);
      if (!isNaN(dateObj)) {
        dateStr = dateObj.toISOString().split('T')[0];
      }
    }

    if (dateStr) {
      dateCounts[dateStr] = (dateCounts[dateStr] || 0) + 1;
    } else {
      console.warn(`No valid date found for threat id=${threat.id}`);
    }
  });

  const sortedDates = Object.keys(dateCounts).sort();
  const counts = sortedDates.map((date) => dateCounts[date]);

  if (sortedDates.length === 0) {
    return <div>No valid date data found in threats.</div>;
  }

  const data = {
    labels: sortedDates,
    datasets: [
      {
        label: 'Threats Over Time',
        data: counts,
        fill: false,
        borderColor: 'rgb(75, 192, 192)',
        backgroundColor: 'rgba(75, 192, 192, 0.2)',
        tension: 0.3,
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false, // important for controlling height/width
    plugins: {
      legend: { position: 'top' },
      tooltip: { mode: 'index', intersect: false },
    },
    scales: {
      x: {
        title: { display: true, text: 'Date' },
      },
      y: {
        title: { display: true, text: 'Threat Count' },
        beginAtZero: true,
        min: 0,
        max: Math.max(...counts) + 1,
      },
    },
  };

  return (
    <div
      style={{
        maxWidth: '600px', // limit the width
        height: '300px', // control the height
        margin: 'auto', // center horizontally
      }}
      className="mt-5"
    >
      <h5 style={{ textAlign: 'center' }}>Threat Trends Over Time</h5>
      <Line data={data} options={options} />
    </div>
  );
};

export default ThreatTrendsChart;
