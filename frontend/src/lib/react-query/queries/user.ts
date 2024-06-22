import {
  useQuery,
  useMutation,
  useQueryClient,
  UseQueryResult
} from "@tanstack/react-query";

import { QUERY_KEYS } from "../queryKeys";
import axios from "axios";
import Cookies from "js-cookie";
import { User } from "../../types";

const baseUrl = "http://localhost:8080";
const ax = axios.create({ baseURL: baseUrl });

export const useGetUsers = (): UseQueryResult<User[], unknown> => {
  return useQuery({
    queryKey: [QUERY_KEYS.GET_USERS],
    queryFn: async () => {
      try {
        const res = await ax.get('/users?page=0&size=1000', {
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
    select: (data) => data?._embedded?.userDTOes
  });
}

export const useGetUserById = ({ id }: User): UseQueryResult<User, unknown> => {
  return useQuery({
    queryKey: [QUERY_KEYS.GET_USER_BY_ID, id],
    queryFn: async () => {
      try {
        const res = await ax.get(`/users/${id}`, {
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
    notifyOnChangeProps: ['data', 'status', 'isPending'],
  });
}

export const useUpdateUser = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationKey: [QUERY_KEYS.UPDATE_USER_BY_ID],
    mutationFn: async (user: User) => {
      try {
        const res = await ax.put(`/users/${user.id}`, user, {
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
        queryKey: [QUERY_KEYS.GET_USER_BY_ID]
      });
    }
  });
}
