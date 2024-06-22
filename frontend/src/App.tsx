import { Route, Routes } from "react-router-dom";

import PageLayout from "./components/common/Layout/PageLayout";
import Home from "./pages/Home/Home";
import UserProfile from "./pages/UserProfile/UserProfile";
import CreatePost from "./pages/CreatePost/CreatePost";
import Notifications from "./pages/Notifications/Notifications";
import Saved from "./pages/Saved/Saved";
import Community from "./pages/Community/Community";
import Chats from "./pages/Chats/Chats";
import EditProfile from "./pages/EditProfile/EditProfile";
import SingleChat from "./pages/SingleChat/SingleChat";
import CreateStory from "./pages/CreateStory/CreateStory";
import Search from "./pages/Search/Search";
import EditPost from "./pages/EditPost/EditPost";
import AuthLayout from "./pages/Auth/auth";
import LoginForm from "./components/Auth/Login";
import SignupForm from "./components/Auth/Signup";
import {Toaster} from '@/components/ui/toaster'
import VerificationForm from "./components/Auth/VerificationForm";

function app() {
  return (
    <>
      <Routes>
        <Route element={<AuthLayout />}>
          <Route path={'login'} element={<LoginForm />} />
          <Route path={'sign-up'} element={<SignupForm />} />
          <Route path="verify-email" element={<VerificationForm />} /> 
        </Route>
        <Route element={<PageLayout />}>
          <Route path="/" element={<Home />} />
          <Route path="/search" element={<Search />} />
          <Route path="/user-profile/:id" element={<UserProfile />} />
          <Route path="/notifications" element={<Notifications />} />
          <Route path="/saved" element={<Saved />} />
          <Route path="/community" element={<Community />} />
          <Route path="/chats" element={<Chats />} />
          <Route path=":selectedUserId" element={<SingleChat />} />
          <Route path="/create-post/:id?" element={<CreatePost />} />
          <Route path="/create-story/" element={<CreateStory />} />
          <Route path="/edit-profile" element={<EditProfile />} />
          <Route path="/edit-post/:id" element={<EditPost />} />
        </Route>
      </Routes>
      <Toaster />
    </>
  );
}

export default app;
