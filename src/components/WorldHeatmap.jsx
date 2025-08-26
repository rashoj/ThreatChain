import React, { useEffect, useState } from 'react';
import ReactECharts from 'echarts-for-react';
import * as echarts from 'echarts/core';
import worldJson from '../assets/maps/world.json'; 

function WorldHeatmap({ data }) {
  const [mapLoaded, setMapLoaded] = useState(false);

  useEffect(() => {
    echarts.registerMap('world', worldJson);
    setMapLoaded(true);
  }, []);

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}',
    },
    visualMap: {
      min: 0,
      max: Math.max(...(data.map(d => d.value)), 10),  // added fallback max=10
      text: ['High', 'Low'],
      realtime: false,
      calculable: true,
      inRange: {
        color: ['#e0ffff', '#006edd'],
      },
    },
    series: [
      {
        name: 'Threats by Country',
        type: 'map',
        map: 'world',
        roam: true,
        emphasis: {
          label: {
            show: true,
          },
        },
        data: data,
      },
    ],
  };

  if (!mapLoaded) {
    return <div>Loading map...</div>;
  }

  return <ReactECharts option={option} style={{ height: 500, width: '100%' }} />;
}

export default WorldHeatmap;
