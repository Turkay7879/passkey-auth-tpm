import axios, { AxiosError, AxiosResponse } from "axios";

const handleError = (err: AxiosError) => {
    if (err && err.response && err.response.status) {
        if (err.response.status === 401) {
            return "You are unauthorized!";
        } else if (err.response.status === 403) {
            return "You are not allowed for this operation!";
        } else if (err.response.status === 500) {
            return err.response.data ?? "Service error!";
        }
    } else {
        return "Service error!";
    }
}

const baseAPIURL = "/api";

export default {
    get: (url: string): Promise<any> => {
        return new Promise((resolve, reject) => {
            axios.get(baseAPIURL + url)
                .then((response: AxiosResponse) => resolve(response.data))
                .catch((err: AxiosError) => reject(handleError(err)))
        })
    },
    post: (url: string, payload: any): Promise<any> => {
        return new Promise((resolve, reject) => {
            axios.post(baseAPIURL + url, payload)
                .then((response: AxiosResponse) => resolve(response.data))
                .catch((err: AxiosError) => reject(handleError(err)))
        })
    },
    delete: (url: string, payload: any): Promise<any> => {
        return new Promise((resolve, reject) => {
            axios.delete(baseAPIURL + url, payload)
                .then((response: AxiosResponse) => resolve(response.data))
                .catch((err: AxiosError) => reject(handleError(err)))
        })
    },
}