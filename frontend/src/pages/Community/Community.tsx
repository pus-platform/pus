//@ts-nocheck
import { useState } from "react";
import { HiOutlineUserGroup } from "react-icons/hi2";
import Heading from "../../components/Heading/Heading";
import PostList from "../../components/PostList/PostList";
import { Spinner } from "flowbite-react";
import communityCover from "../../assets/BU.jpeg";
import { useGetCommunities, useGetCommunityPosts } from "@/lib/react-query/queries/community";
import { useUserContext } from "@/context/AuthContext";

const Community: React.FC = () => {
  const { user } = useUserContext();
  const { data: communities } = useGetCommunities();
  const communityId = communities?.find((community) => community.name === user?.community)?.id;
  const { data: posts, isPending } = useGetCommunityPosts({ id: communityId });
  const [coverImage, setCoverImage] = useState<string>(communityCover);

  const formatCommunityName = (name: string | undefined) => {
    if (!name) return "";
    return name
      .split("_")
      .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
      .join(" ");
  };

  const About: React.FC = () => {
    return (
      <div className="bg-purple-500 text-white h-fit p-5 rounded-md shadow-lg w-full md:w-1/3">
        <div className="flex items-center space-x-3 mb-5">
          <h2 className="text-white text-xl font-bold">About Community</h2>
        </div>
        <p className="mt-4">
          Welcome to the {formatCommunityName(user?.community)}! This platform is dedicated to bringing together students, faculty, and alumni to share news, events, and discussions. Connect, engage, and grow with us.
        </p>
        <h2 className="mt-4 text-lg font-bold">Our Mission</h2>
        <p>
          To foster a collaborative and supportive environment for all members of the {formatCommunityName(user?.community)} community.
        </p>
        <h2 className="mt-4 text-lg font-bold">Contact Us</h2>
        <p>
          Email: community@university.edu
        </p>
      </div>
    );
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      const reader = new FileReader();
      reader.onloadend = () => {
        setCoverImage(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  return (
    <div className="dark:text-white h-full w-full px-2 md:px-8 pb-40">
      <Heading
        icon={<HiOutlineUserGroup className="h-8 w-8" />}
        label="Community"
      />
      <div className="space-y-10">
        <div className="h-[300px] w-full rounded-md overflow-hidden relative">
          <img src={coverImage} className="w-full h-full object-cover" />
          <input
            id="fileInput"
            type="file"
            accept="image/*"
            className="hidden"
            onChange={handleImageChange}
          />
        </div>
        <div className="flex md:flex-row flex-col gap-5 justify-between w-full items-start ">
          <div className="flex flex-col space-y-8">
            <h1 className="text-lg md:text-4xl font-bold capitalize whitespace-nowrap">
              {formatCommunityName(user?.community)}
            </h1>
          </div>
        </div>
      </div>
      <hr className="border-b border-gray-200 dark:border-gray-700 my-5" />
      <div className="flex flex-col md:flex-row md:space-x-10">
        <div className="md:w-2/3">
          <Heading label="Posts" />
          <div className="flex justify-center md:mx-auto w-full">
            {isPending ? (
              <div className="flex h-full pt-40 w-full justify-center items-center">
                <Spinner color="purple" size="xl" aria-label="Purple spinner" />
              </div>
            ) : posts?.length > 0 ? (
              <div className="!my-10 w-full flex justify-center">
                <PostList posts={posts} />
              </div>
            ) : (
              <div className="flex h-full pt-40 w-full justify-center items-center">
                <p>There are no posts to display</p>
              </div>
            )}
          </div>
        </div>
        <About />
      </div>
    </div>
  );
};

export default Community;
