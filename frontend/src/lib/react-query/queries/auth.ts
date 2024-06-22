import {
    useMutation,
    useQueryClient,
} from "@tanstack/react-query";

import { QUERY_KEYS } from "../queryKeys";
import axios from "axios";
import Cookies from "js-cookie";
import { User, SignupUser, LoginUser } from "../../types";

const baseUrl = "http://localhost:8080";
const ax = axios.create({ baseURL: baseUrl })

export const useCreateNewUser = () => {
    return useMutation({
        mutationKey: [QUERY_KEYS.CREATE_USER_ACCOUNT],
        mutationFn: async (user: SignupUser)=> {
            const res = await ax.post('/signup', {
                fullname: user.fullname,
                username: user.username,
                email: user.email,
                password: user.password,
                university: "BETHLEHEM_UNIVERSITY"
            }, {
                headers: {
                    "Content-Type": "application/json",
                }
            });
            return res.data;
        }
    })
}

export const useSignOutUser = () => {
    const queryClient = useQueryClient()
    return useMutation({
        mutationKey: [QUERY_KEYS.LOGOUT_USER],
        mutationFn: async () => {
            try {
                Cookies.remove('token');
            } catch (error) {
                console.log(error);
            }
        },
        onSettled: () => {
            window.location.href = '/login';
            queryClient.removeQueries({
                queryKey: [QUERY_KEYS.GET_CURRENT_USER]
            });
        }
    })
}

export async function useGetCurrentUser(): Promise<User> {
    const res = await ax.get('/current-user', {
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${Cookies.get("token")}`
        }
    });
    return res.data;
}

export const useLoginUser = () => {
    return useMutation({
        mutationKey: [QUERY_KEYS.LOGIN_USER],
        mutationFn: async (user: LoginUser) => {
            const res = await ax.post('/login', {
                username: user.username,
                password: user.password,
            }, {
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${Cookies.get("token")}`
                }
            });
            return res.data;
        }
    })
}
