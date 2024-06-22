import { useEffect, useState } from "react";
import { Sidebar, CustomFlowbiteTheme, Flowbite } from "flowbite-react";
import { DarkThemeToggle } from "flowbite-react";
import { LogOut,MessageCircle } from 'lucide-react';
import { Bookmark } from 'lucide-react';
import { HiOutlineHome, HiOutlineUserGroup } from "react-icons/hi2";
import { SquarePlus } from 'lucide-react';
import Dropdown_ from "../common/Dropdown/Dropdown";
import { Bell } from 'lucide-react';
import MyAvatar from "../common/MyAvatar/MyAvatar";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useMediaQuery } from "@mui/material";
import { Search } from 'lucide-react';
import { AlignJustify } from 'lucide-react';

import { useSignOutUser } from "@/lib/react-query/queries/auth";
import { useUserContext } from "@/context/AuthContext";

const sidebarTheme: CustomFlowbiteTheme = {
  sidebar: {
    root: {
      base: "h-screen hidden md:block max-w-[100px] lg:max-w-full",
      collapsed: {
        on: "w-16",
        off: "w-64",
      },
      inner:
        "h-full overflow-y-auto overflow-x-hidden rounded bg-gray-100 lg:px-[24px] pt-[24px] pb-[32px] dark:bg-[#101012]",
    },
    collapse: {
      button:
        "group flex w-full items-center rounded-lg p-2 text-sm lg:text-base font-normal text-gray-900 transition duration-75 hover:bg-purple-300 dark:text-white dark:hover:purple-300 cursor-pointer",
      icon: {
        base: "lg:h-6 lg:w-6 h-48 w-48 text-gray-500 transition duration-75 group-hover:text-gray-900 dark:text-gray-400 dark:group-hover:text-white",
        open: {
          off: "",
          on: "text-gray-900",
        },
      },
      label: {
        base: "flex-1 whitespace-nowrap text-left",
        icon: {
          base: "h-6 w-6 transition delay-0 ease-in-ou",
          open: {
            on: "rotate-180",
            off: "",
          },
        },
      },
      list: "space-y-[24px] py-2",
    },

    item: {
      base: "group flex items-center lg:justify-center rounded-lg p-2 lg:py-3 text-sm lg:text-[14px] font-[600] text-black hover:bg-purple-500 dark:text-white dark:hover:bg-purple-500 max-w-fit lg:max-w-full mx-auto lg:flex-row flex-col ",
      active: "bg-purple-500 dark:bg-purple-500 text-white",
      collapsed: {
        insideCollapse: "group w-full transition duration-75",
        noIcon: "font-bold",
      },
      content: {
        base: "hidden lg:flex flex-1 whitespace-nowrap px-3",
      },
      icon: {
        base: "h-[28px] w-[28px] lg:h-[24px] lg:w-[24px] flex-shrink-0 text-gray-500 transition duration-75  text-purple-500 group-hover:text-white",
        active: "text-white dark:text-white",
      },
      label: "hidden lg:flex",
      listItem: "",
    },
  },
};

const CustomSideBar = () => {
  const [activeItem, setActiveItem] = useState("");
  const [col, setCol] = useState(false);
  const navigate = useNavigate();
  const isTablet = useMediaQuery("(min-width:600px) and (max-width:900px)");
  const location = useLocation();
  const { pathname } = location;
  const {user} = useUserContext();
  const { mutateAsync: logout} = useSignOutUser();

  const handleClick = (route: string) => {
    setActiveItem(route);
    navigate(`/${route}`);
  };

  useEffect(() => {
    isTablet ? setCol(true) : setCol(false);
  }, [isTablet]);

  return (
    <Flowbite theme={{ theme: sidebarTheme }}>
      <Sidebar aria-label="Default h-screen box sidebar example">
        <Link
          to="/"
          className="flex space-x-2 items-center justify-center lg:justify-start"
        >
          <img src="src/assets/logo.png" alt="logo" className="w-20" />
        </Link>
        <div className="my-[40px] mx-auto w-full flex justify-center lg:justify-start">
          <MyAvatar
            id={user?.id}
            hasUsername
            isPost={false}
            hasName
            size="xs"
            row={!col}
            col={col}
          />
        </div>
        <Sidebar.Items className="h-[30%] flex flex-col justify-between  ">
          <Sidebar.ItemGroup>
            <Sidebar.Item
              icon={HiOutlineHome}
              active={pathname == "/"}
              className="hover:cursor-pointer"
              onClick={() => handleClick("#")}
            >
              Home
            </Sidebar.Item>
            <Sidebar.Item
              icon={Search}
              active={pathname == "/search"}
              className="hover:cursor-pointer"
              onClick={() => handleClick("search")}
            >
              Search
            </Sidebar.Item>
            <Sidebar.Item
              icon={HiOutlineUserGroup}
              active={pathname == "/community"}
              className="hover:cursor-pointer"
              onClick={() => handleClick("community")}
            >
              Community
            </Sidebar.Item>
            <Sidebar.Item
              icon={Bookmark}
              active={pathname == "/saved"}
              className="hover:cursor-pointer"
              onClick={() => handleClick("saved")}
            >
              Saved
            </Sidebar.Item>
            <Sidebar.Item
              className="hover:cursor-pointer"
              icon={MessageCircle}
              active={pathname == "/chats"}
              onClick={() => handleClick("chats")}
            >
              Chats
            </Sidebar.Item>
            <Sidebar.Item
              icon={Bell}
              labelColor="yellow"
              className="hover:cursor-pointer"
              active={pathname == "/notifications"}
              onClick={() => handleClick("notifications")}
            >
              Notifications
            </Sidebar.Item>
            <Sidebar.Item
              icon={SquarePlus}
              active={pathname == "/create-post"}
              className="hover:cursor-pointer"
              onClick={() => handleClick("create-post")}
            >
              Create Post
            </Sidebar.Item>
          </Sidebar.ItemGroup>
          <div className="w-full flex justify-center lg:justify-start mt-[24px]">
            <Dropdown_
              icon={
                <AlignJustify color="#877EFF" className="w-[28px] h-[28px] lg:w-[24px] lg:h-[24px] text-purple-500" />
              }
              items={[
                {
                  icon: <DarkThemeToggle />,
                  label: "ToggleTheme",
                },
                {
                  icon:<LogOut color="#877EFF" className="h-6 w-6 ml-1.5 mr-2" />,
                  label: "Logout",
                  onClick: logout
                },
              ]}
              label="More"
            />
          </div>
        </Sidebar.Items>
      </Sidebar>
    </Flowbite>
  );
};

export default CustomSideBar;
