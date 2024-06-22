import {
    useQuery,
    UseQueryResult,
} from "@tanstack/react-query";

import { QUERY_KEYS } from "../queryKeys";
import axios from "axios";
import Cookies from "js-cookie";
import { Notification } from "@/lib/types";

const baseUrl = "http://localhost:8080";
const ax = axios.create({ baseURL: baseUrl });

export const useGetNotifications = (): UseQueryResult<Notification[], unknown> => {
    return useQuery({
        queryKey: [QUERY_KEYS.GET_NOTIFICATIONS],
        queryFn: async () => {
            try {
                const res = await ax.get(`/notifications?page=0&size=1000`, {
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
        notifyOnChangeProps: ['data', 'status', 'isSuccess'],
        select: (data) => data?._embedded?.notificationDTOes,
        refetchInterval: 1000,
    });
}
