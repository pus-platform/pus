import {
    useQuery,
    useMutation,
    useQueryClient,
    UseQueryResult,
} from "@tanstack/react-query";

import { QUERY_KEYS } from "../queryKeys";
import axios from "axios";
import Cookies from "js-cookie";
import { SectionCommunity as Section, User } from "../../types";

const baseUrl = "http://localhost:8080";
const ax = axios.create({ baseURL: baseUrl });

export const useGetCourses = () => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_COURSES],
        queryFn: async () => {
            try {
                const res = await ax.get(`/communities/courses`, {
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
        }
    });
}

export const useGetSectionStudents = ({ id }: Section): UseQueryResult<User, unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_SECTION_STUDENTS, id],
        queryFn: async () => {
            try {
                const res = await ax.get(`/sections/${id}/students?page=0&size=1000`, {
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
        select: (data) => data?._embedded?.users
    });
}

export const useAddSectionStudent = ({ id }: Section) => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.ADD_SECTION_STUDENT, id],
        mutationFn: async ({ userId }: Section) => {
            try {
                const res = await ax.post(`/sections/${id}/students`, {
                    user: userId
                }, {
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
                queryKey: [QUERY_KEYS.GET_SECTION_STUDENTS, id]
            });
        }
    });
}

export const useAddSection = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationKey: [QUERY_KEYS.CREATE_SECTION],
        mutationFn: async (data: Section) => {
            try {
                const res = await ax.post(`/sections`, data, {
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
                queryKey: [QUERY_KEYS.GET_SECTIONS]
            });
        }
    });
}
