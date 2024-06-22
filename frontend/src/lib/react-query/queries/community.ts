import { useQuery, UseQueryResult } from "@tanstack/react-query";

import { QUERY_KEYS } from "../queryKeys";
import axios from "axios";
import Cookies from "js-cookie";
import { Community, Post, Story, User } from "../../types";

const baseUrl = "http://localhost:8080";
const ax = axios.create({ baseURL: baseUrl });

export const useGetCommunityPosts = ({ id }: Community): UseQueryResult<Post[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_COMMUNITY_POSTS, id],
        queryFn: async () => {
            try {
                const res = await ax.get(`communities/${id}/posts?page=0&size=1000`, {
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${Cookies.get('token')}`
                    }
                });
                return res.data;
            } catch (error) {
                console.error(error.message);
                return [];
            }
        },
        enabled: !!id,
        select: (data) => data?._embedded?.postDTOes,
        notifyOnChangeProps: ['data', 'status', 'isSuccess'],
    });
}

export const useGetCommunityStories = ({ id }: Community): UseQueryResult<Story[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_COMMUNITY_STORIES, id],
        queryFn: async () => {
            try {
                const res = await ax.get(`communities/${id}/stories?page=0&size=1000`, {
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${Cookies.get('token')}`
                    }
                });
                return res.data;
            } catch (error) {
                console.error(error.message);
                return [];
            }
        },
        enabled: !!id,
        select: (data) => data?._embedded?.storyDTOes,
        notifyOnChangeProps: ['data', 'status', 'isSuccess'],
    });
}

export const useGetCommunityMembers = ({ id }: Community): UseQueryResult<User[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_COMMUNITY_MEMBERS, id],
        queryFn: async () => {
            try {
                const res = await ax.get(`communities/${id}/members?page=0&size=1000`, {
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${Cookies.get('token')}`
                    }
                });
                return res.data;
            } catch (error) {
                console.error(error.message);
                return [];
            }
        },
        enabled: !!id,
        select: (data) => data?._embedded?.users,
        notifyOnChangeProps: ['data', 'status', 'isSuccess'],
    });
}

export const useGetCommunity = ({ id }: Community): UseQueryResult<Community, unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_COMMUNITY_BY_ID, id],
        queryFn: async () => {
            try {
                const res = await ax.get(`communities/${id}`, {
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${Cookies.get('token')}`
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
    });
}

export const useGetCommunities = (): UseQueryResult<Community[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_COMMUNITIES],
        queryFn: async () => {
            try {
                const res = await ax.get('communities?page=0&size=20', {
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${Cookies.get('token')}`
                    }
                });
                return res.data;
            } catch (error) {
                console.error(error.message);
                return [];
            }
        },
        notifyOnChangeProps: ['data', 'status', 'isSuccess'],
        select: (data) => data?._embedded?.communityDTOes,
    });
}