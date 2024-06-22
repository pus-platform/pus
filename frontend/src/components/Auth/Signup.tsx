import { useForm, SubmitHandler } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";
import { zodResolver } from "@hookform/resolvers/zod";

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
import Loader from "../common/Loader/Loader";

import { useCreateNewUser } from "../../lib/react-query/queries/auth";
import { SignupValidation } from "../../lib/validation";
import { SignupUser } from "../../lib/types";

const SignupForm: React.FC = () => {
    const { toast } = useToast();
    const navigate = useNavigate();

    const form = useForm<SignupUser>({
        resolver: zodResolver(SignupValidation),
        defaultValues: {
            fullname: "",
            username: "",
            email: "",
            password: "",
        },
    });

    const { mutateAsync: createNewUser, isPending: isCreatingUser } = useCreateNewUser();

    const handleSignup: SubmitHandler<SignupUser> = async (user) => {
        try {
            const newUser = await createNewUser(user);
            if (!newUser) {
                toast({ title: "Sign up failed. Please try again." });
                return;
            }

            navigate("/verify-email");

        } catch (error) {
            console.log({ error });
            toast({ title: "An error occurred. Please try again." });
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen p-0 m-0" style={{ paddingTop: '0px' }}>
            <Form {...form}>
                <div className="loginCard w-96 p-8 bg-white rounded-md shadow-md">
                    <div className="flex justify-center mb-6">
                        <img src="src/assets/logo.png" alt="PU Logo" className="w-61 h-40" />
                    </div>
                    <form onSubmit={form.handleSubmit(handleSignup)} className="space-y-4">
                        <FormField
                            control={form.control}
                            name="fullname"
                            render={({ field }) => (
                                <FormItem>
                                    <FormControl>
                                        <Input
                                            type="text"
                                            placeholder="Full Name"
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
                            name="username"
                            render={({ field }) => (
                                <FormItem>
                                    <FormControl>
                                        <Input
                                            type="text"
                                            placeholder="Username"
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
                            name="email"
                            render={({ field }) => (
                                <FormItem>
                                    <FormControl>
                                        <Input
                                            type="email"
                                            placeholder="Email"
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
                            name="password"
                            render={({ field }) => (
                                <FormItem>
                                    <FormControl>
                                        <Input
                                            type="password"
                                            placeholder="Password"
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
                            {isCreatingUser ? (
                                <Loader />
                            ) : (
                                "Sign Up"
                            )}
                        </Button>
                        <p className="mt-4 text-sm text-center text-gray-500">
                            Already have an account?
                            <Link to="/login" className="ml-1 font-semibold text-[#586CC3] hover:ring-[#8F9FE2]" >
                                Log in
                            </Link>
                        </p>
                    </form>
                </div>
            </Form>
        </div>
    );
};

export default SignupForm;
