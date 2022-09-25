import axios from 'axios';
import authHeader from './auth-header';
const API_URL = '/api/user/';

class UserService {

  getAccounts(user) {
    return axios
      .get(API_URL + user.id  + '/accounts', { headers: authHeader() })
      .then(response => {
        return response.data;
      });
  }

  createAccount(user, accountContent) {
    return axios
      .post(API_URL + user.id  + '/accounts', accountContent, { headers: authHeader() })
      .then(response => {
        return response.data;
      });
  }

  deleteAccount(user, externalIdentifier) {
    return axios
      .delete(API_URL + user.id  + '/accounts/' + externalIdentifier, { headers: authHeader() })
      .then();
  }
}

export default new UserService();