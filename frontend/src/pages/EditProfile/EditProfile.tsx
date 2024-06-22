import React, { useState, useRef } from "react";
import Heading from "../../components/Heading/Heading";
import { Pencil } from 'lucide-react';
import Textarea from "../../components/common/Textarea/Textarea";
import TextInput from "../../components/common/TextInput/TextInput";
import Button from "../../components/common/Button/Button";
import { useUserContext } from "@/context/AuthContext";
import { User } from "@/lib/types";
import { useUpdateUser } from "@/lib/react-query/queries";
import { useNavigate } from "react-router-dom";
import MyAvatar from "@/components/common/MyAvatar/MyAvatar";

const EditProfile = () => {
  const { mutate: updateUser } = useUpdateUser();
  const { user } = useUserContext();
  const navigate = useNavigate();
  const [userData, setUserData] = useState<User>({
    id: user?.id,
    fullname: user?.fullname,
    username: user?.username,
    email: user?.email,
    bio: user?.bio,
    imageUrl: user?.imageUrl,
  });
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setUserData({
      ...userData,
      [name]: value,
    });
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        const base64data = reader.result as string;

        setUserData({
          ...userData,
          imageUrl: base64data,
        });
      };
      reader.readAsDataURL(file);
    }
  };

  const handleButtonClick = () => {
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  };

  const handleSubmit = async () => {
    updateUser(userData);
    navigate(-1);
  };

  const formatCommunityName = (name: string | undefined) => {
    if (!name) return "";
    return name
      .split("_")
      .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
      .join(" ");
  };

  return (
    <div className="flex w-full dark:text-white min-h-screen">
      <div className="w-full flex flex-col space-y-8 align-center px-2 md:px-10 lg:px-20 min-h-screen">
        <Heading
          icon={<Pencil className="h-8 w-8" />}
          label="Edit Profile"
        />
        <div className="flex space-x-6 items-center">
          <MyAvatar
            id={userData?.id}
            size="2xl"
            col />
          <button className="text-blue-500" onClick={handleButtonClick}>
            Change Profile Picture
          </button>
          <input
            type="file"
            className="hidden"
            ref={fileInputRef}
            onChange={handleFileChange}
          />
        </div>
        <TextInput
          label="Fullname"
          type="text"
          name="fullname"
          value={userData?.fullname}
          onChange={handleChange}
        />
        <TextInput
          label="Username"
          type="text"
          name="username"
          value={userData?.username}
          onChange={handleChange}
        />
        <TextInput
          label="Email"
          type="email"
          name="email"
          value={userData?.email}
          onChange={handleChange}
        />
        <TextInput
          label="University"
          type="text"
          name="community"
          // @ts-ignore
          value={formatCommunityName(user?.community)}
        />
        <Textarea
          value={userData?.bio}
          label="Bio"
          placeholder="Bio"
          name="bio"
          onChange={handleChange}
        />
        <div className="ml-auto my-6">
          <Button
            label="Save Changes"
            size="sm"
            color="purpleFit"
            onClick={handleSubmit}
            style={{ marginBottom: "24px" }}
          />
        </div>
      </div>
    </div>
  );
};

export default EditProfile;
