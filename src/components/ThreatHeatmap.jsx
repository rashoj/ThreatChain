import React, { useEffect, useState } from 'react';
import ReactECharts from 'echarts-for-react';
import axios from 'axios';

const ThreatHeatmap = () => {
  const [heatmapData, setHeatmapData] = useState([]);

  useEffect(() => {
    axios.get('/api/threat-summaries')
      .then((response) => {
        const summaries = response.data;

        // Count threats per country
        const countryCount = {};
        summaries.forEach(threat => {
          const countries = threat.countries?.split(',').map(c => c.trim()) || [];
          countries.forEach(country => {
            countryCount[country] = (countryCount[country] || 0) + 1;
          });
        });

        // Map to format required by echarts
        const formatted = Object.entries(countryCount).map(([name, value]) => ({ name, value }));
        setHeatmapData(formatted);
      })
      .catch(err => console.error('Error fetching threat data:', err));
  }, []);

  const option = {
    title: {
      text: 'Threat Heatmap by Country',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} threats'
    },
    visualMap: {
      min: 0,
      max: Math.max(...heatmapData.map(d => d.value), 10),
      text: ['High', 'Low'],
      realtime: true,
      calculable: true,
      inRange: {
        color: ['#e0f3f8', '#abd9e9', '#74add1', '#4575b4']
      }
    },
    series: [{
      type: 'map',
      map: 'world',
      roam: true,
      data: heatmapData
    }]
  };

  return <ReactECharts option={option} style={{ height: '600px', width: '100%' }} />;
};

export default ThreatHeatmap;
