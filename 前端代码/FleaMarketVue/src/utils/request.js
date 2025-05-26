import axios from 'axios';

const service = axios.create({
    timeout: 5000,
    baseURL:  'http://localhost:9321',
    withCredentials:  true
});

service.interceptors.request.use(
    config => {
        return config;
    },
    error => {
        console.log(error);
        return Promise.reject();
    }
);

service.interceptors.response.use(
    response => {
        if (response.status === 200) {
            return response.data;
        } else {
            Promise.reject();
        }
    },
    e => {
        console.log(e);
        return Promise.reject(e);
    }
);

import '../../initialize'
export default service;
