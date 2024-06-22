import { useForm, SubmitHandler } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Link, useNavigate } from "react-router-dom";
import Cookies from 'js-cookie';
import { GoogleLogin, CredentialResponse } from "@react-oauth/google";
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
import Loader from "../common/Loader/Loader";
import { LoginValidation } from "../../lib/validation";
import { LoginUser } from "../../lib/types";
import { useLoginUser } from "../../lib/react-query/queries/auth";
import { useUserContext } from "../../context/AuthContext";
import { jwtDecode } from "jwt-decode";

const baseUrl = "http://localhost:8080";
const ax = axios.create({
    baseURL: baseUrl,
    headers: {
        "Content-Type": "application/json",
    }
});

const LoginForm: React.FC = () => {
    const { toast } = useToast();
    const navigate = useNavigate();
    const { checkAuthUser, isLoading: isUserLoading } = useUserContext();
    const { mutateAsync: signInAccount, isPending } = useLoginUser();

    const form = useForm<LoginUser>({
        resolver: zodResolver(LoginValidation),
        defaultValues: {
            username: "",
            password: "",
        },
    });

    const handleGoogle = async (response: CredentialResponse) => {
        const email = jwtDecode<{ email: string }>(response.credential!).email;
        const token = await ax.post("/get-google-token", email);
        Cookies.set('token', token.data, { expires: 1 });
        await checkAuthUser();
        navigate("/");
    };

    const handleLogin: SubmitHandler<LoginUser> = async (user) => {
        const session = await signInAccount(user);

        if (!session) {
            toast({ title: "Login failed. Please try again." });
            return;
        }

        Cookies.set('token', session.token, { expires: 1 });
        const isLoggedIn = await checkAuthUser();

        if (isLoggedIn && !isUserLoading) {
            form.reset();
            navigate("/");
        } else {
            toast({ title: "Login failed. Please try again." });
            return;
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen p-0 m-0" style={{
            paddingTop: '0px',
        }}>
            <Form {...form}>
                <div className="loginCard w-96 p-8 bg-white rounded-md shadow-md">
                    <div className="flex justify-center mb-6">
                        <img src="src/assets/logo.png" alt="PU Logo" className="w-61 h-40" />
                    </div>
                    <form onSubmit={form.handleSubmit(handleLogin)} className="space-y-4">
                        <FormField
                            control={form.control}
                            name="username"
                            render={({ field }) => (
                                <FormItem>
                                    <FormControl>
                                        <Input
                                            type="text"
                                            placeholder="Phone number, username, or email"
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
                            {isPending || isUserLoading ? (
                                <Loader />
                            ) : (
                                "Log in"
                            )}
                        </Button>
                        <div className="relative flex items-center justify-center my-4" style={{ marginTop: "30px", marginBottom: "10px" }}>
                            <span className="absolute px-4 bg-white text-gray-500">OR</span>
                            <hr className="w-full border-t border-gray-300" />
                        </div>
                        <div style={{ marginTop: "30px" }}>
                            <div className="flex items-center justify-center">
                                <GoogleLogin
                                    onSuccess={(credentialResponse) => handleGoogle(credentialResponse)}
                                    width={320}
                                    onError={() => {
                                        console.log('Login Failed');
                                    }}
                                />
                            </div>
                        </div>
                        <p className="mt-4 text-sm text-center text-gray-500">
                            Don't have an account?
                            <Link
                                to="/sign-up"
                                className="ml-1 font-semibold text-[#586CC3] hover:ring-[#8F9FE2]"
                            >
                                Sign up
                            </Link>
                        </p>
                    </form>
                </div>
            </Form>
        </div>
    );
};

export default LoginForm;
