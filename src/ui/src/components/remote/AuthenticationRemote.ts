import RestController from "./RestController";

const authenticationBaseURL = "/userAuth";

type LoginPayload = {
    mail: string,
    password: string
}

export default {
    login: (payload: LoginPayload): Promise<any> => {
        return RestController.post(authenticationBaseURL + "/login", payload);
    }
};

export type { LoginPayload };