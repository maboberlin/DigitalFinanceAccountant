import axios from 'axios';
import authHeader from './auth-header';
const API_URL = '/api/auth/';

class AuthService {

  login(user) {
    return axios
      .post(API_URL + 'signIn', {
        email: user.email,
        password: user.password
      })
      .then(response => {
        if (response.data.accessToken) {
          localStorage.setItem('user', JSON.stringify(response.data));
        }
        return response.data;
      });
  }

  logout() {
    localStorage.removeItem('user');
  }

  register(user) {
    return axios.post(API_URL + 'signUp', {
      foreName: user.foreName,
      surName: user.surName,
      mailAddress: user.email,
      password: user.password
    });
  }

  refresh() {
    return axios.get(API_URL + 'refresh', { headers: authHeader() })
      .then(response => {
        if (response.data.accessToken) {
          localStorage.setItem('user', JSON.stringify(response.data));
        }
        return response.data;
      });
  }

}

export default new AuthService();