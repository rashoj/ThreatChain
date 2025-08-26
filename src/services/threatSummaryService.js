import axios from 'axios';

const API_URL = 'http://localhost:8081/api/summaries/paginated';

export const fetchPaginatedThreatSummaries = async (page = 0, size = 5, sortBy = 'risk_score', order = 'desc') => {
  const response = await axios.get(API_URL, {
    params: { page, size, sortBy, order },
    headers: {
      'x-api-key': 'e3a490ef03b1bb5a89cb9b57cf5087dc5794b8e9774e1a8bda6540ed59932650' // replace with actual key
    }
  });
  return response.data;
};
