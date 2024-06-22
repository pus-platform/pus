//@ts-nocheck
import React, { useState } from "react";
import { useForm, SubmitHandler } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import {
  useToast,
  Button,
  Input,
  Form,
  FormControl,
  FormField,
  FormItem,
  FormMessage,
} from "../ui";
import { VerificationValidation } from "../../lib/validation";
import { VerificationCode } from "../../lib/types";
import Loader from "../common/Loader/Loader";

const baseUrl = "http://localhost:8080";
const ax = axios.create({
  baseURL: baseUrl,
  headers: {
    "Content-Type": "application/json",
  },
});

const VerificationForm: React.FC = () => {
  const { toast } = useToast();
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);

  const form = useForm<VerificationCode>({
    resolver: zodResolver(VerificationValidation),
    defaultValues: {
      code: "",
      username: "",
    },
  });

  const handleVerification: SubmitHandler<VerificationCode> = async (data) => {
    console.log(data)
    setIsLoading(true);
    try {
      const response = await ax.post("/verify", data);
      if (response.status === 200) {
        toast({ title: "Verification successful. Please log in." });
        navigate("/login");
      } else {
        toast({ title: "Verification failed. Please try again." });
      }
    } catch (error) {
      console.log(error)
      toast({ title: "Verification failed. Please try again." });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen p-0 m-0">
      <Form {...form}>
        <div className="loginCard w-96 p-8 bg-white rounded-md shadow-md">
          <form onSubmit={form.handleSubmit(handleVerification)} className="space-y-4">
            <FormField
              control={form.control}
              name="username"
              render={({ field }) => (
                <FormItem>
                  <FormControl>
                    <Input
                      type="text"
                      placeholder="Enter username again for verification"
                      className="w-full px-4 py-2 text-sm border rounded-md focus:ring-2 focus:ring-[#586CC3]"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="code"
              render={({ field }) => (
                <FormItem>
                  <FormControl>
                    <Input
                      type="text"
                      placeholder="Enter 6-digit code"
                      className="w-full px-4 py-2 text-sm border rounded-md focus:ring-2 focus:ring-[#586CC3]"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <Button
              type="submit"
              className="w-full py-2 text-white bg-[#586CC3] rounded-md hover:ring-[#8F9FE2] focus:outline-none focus:ring-2 focus:ring-[#586CC3]"
            >
              {isLoading ? <Loader /> : "Verify"}
            </Button>
          </form>
        </div>
      </Form>
    </div>
  );
};

export default VerificationForm;
