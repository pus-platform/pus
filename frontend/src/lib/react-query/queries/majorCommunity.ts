import {
    useQuery,
    useMutation,
    useQueryClient,
    UseQueryResult,
} from "@tanstack/react-query";

import { QUERY_KEYS } from "../queryKeys";
import axios from "axios";
import Cookies from "js-cookie";
import { MajorCommunity as Major, User } from "../../types";

const baseUrl = "http://localhost:8080";
const ax = axios.create({ baseURL: baseUrl });

export const useGetMajors = () => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_MAJORS],
        queryFn: async () => {
            try {
                const res = await ax.get(`/communities/majors`, {
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${Cookies.get("token")}`
                    }
                });
                return res.data;
            } catch (error) {
                console.error(error.message);
                return [];
            }
        },
    });
}

export const useGetMajorStudents = ({ id }: Major): UseQueryResult<User[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_MAJOR_COMMUNITY_STUDENTS, id],
        queryFn: async () => {
            try {
                const res = await ax.get(`/major-communities/${id}/students?page=0&size=1000`, {
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${Cookies.get("token")}`
                    }
                });
                return res.data;
            } catch (error) {
                console.error(error.message);
                return [];
            }
        },
        enabled: !!id,
        notifyOnChangeProps: ['data', 'status', 'isSuccess'],
        select: (data) => data?._embedded?.users,
    });
}

export const useCreateMajor = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.CREATE_MAJOR_COMMUNITY],
        mutationFn: async (data: Major) => {
            try {
                const res = await ax.post(`/major-communities`, data, {
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${Cookies.get("token")}`
                    }
                });
                return res.data;
            } catch (error) {
                console.error(error.message);
                return [];
            }
        },
        onSettled: () => {
            queryClient.invalidateQueries({
                queryKey: [QUERY_KEYS.GET_MAJORS]
            });
        },
    });
}

export const useAddMajorStudent = ({ id }: Major) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.ADD_MAJOR_COMMUNITY_STUDENT, id],
        mutationFn: async () => {
            try {
                const res = await ax.post(`/major-communities/${id}/students`, {}, {
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${Cookies.get("token")}`
                    }
                });
                return res.data;
            } catch (error) {
                console.error(error.message);
                return [];
            }
        },
        onSettled: () => {
            queryClient.invalidateQueries({
                queryKey: [QUERY_KEYS.GET_MAJOR_COMMUNITY_STUDENTS, id]
            });
        },
    });
}
