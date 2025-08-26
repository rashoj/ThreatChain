import * as echarts from 'echarts';
import world from 'echarts/map/json/world.json';

export function loadWorldMap() {
  echarts.registerMap('world', world);
}
