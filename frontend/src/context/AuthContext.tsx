import { useLocation, useNavigate } from "react-router-dom";
import { createContext, useContext, useEffect, useState, ReactNode } from "react";
import Cookies from 'js-cookie';
import { useGetCurrentUser } from "../lib/react-query/queries";
import { User } from "../lib/types";

interface AuthState {
    user: User;
    isLoading: boolean;
    isAuthenticated: boolean;
    setUser: (user: User) => void;
    setIsAuthenticated: (isAuthenticated: boolean) => void;
    checkAuthUser: () => Promise<boolean>;
}

export const INITIAL_USER: User = {};

const INITIAL_STATE: AuthState = {
    user: INITIAL_USER,
    isLoading: false,
    isAuthenticated: false,
    setUser: () => {},
    setIsAuthenticated: () => {},
    checkAuthUser: async () => false,
};

const AuthContext = createContext<AuthState>(INITIAL_STATE);

interface AuthProviderProps {
    children: ReactNode;
}

export default function AuthProvider({ children }: AuthProviderProps) {
    const navigate = useNavigate();
    const [user, setUser] = useState<User>(INITIAL_USER);
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const {pathname} = useLocation();

    const checkAuthUser = async (): Promise<boolean> => {
        setIsLoading(true);
        try {
            const token = Cookies.get("token");
            if (!token) return false;
            
            const response = await useGetCurrentUser();
            const { id, username, fullname, email, dob, password, isActive, bio, gender, community, followers, following, stories, bookmarks, likes, links } = response;
            
            if (id) {
                setUser({ id, username, fullname, dob, email, password, isActive, bio, gender, community, followers, following, stories, bookmarks, likes, links });
                setIsAuthenticated(true);
                return true;
            }
            return false;
        } catch (error) {
            console.error(error.message);
            return false;
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        const token = Cookies.get("token");
        if (!token && pathname !== '/sign-up' && pathname !== '/verify-email') {
            navigate("/login");
        } else {
            checkAuthUser();
        }
    }, [navigate]);

    const value = {
        user,
        setUser,
        isLoading,
        isAuthenticated,
        setIsAuthenticated,
        checkAuthUser,
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}

export const useUserContext = () => useContext(AuthContext);
