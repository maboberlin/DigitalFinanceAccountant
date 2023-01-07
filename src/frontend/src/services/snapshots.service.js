import axios from 'axios';
import authHeader from './auth-header';
const API_URL = '/api/snapshots/';

class SnapshotsService {

  getSnapshots(userIdentifier) {
    return axios
      .get(API_URL + userIdentifier, { headers: authHeader() })
      .then(response => {
        return response.data;
      });
  }

  createSnapshot(userIdentifier, accountIdentifier, currency) {
    return axios
      .post(API_URL + userIdentifier + '/' + accountIdentifier + '/?currency=' + currency, {}, { headers: authHeader() })
      .then(response => {
        return response.data;
      });
  }
}

export default new SnapshotsService();