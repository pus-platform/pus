"use client";

import {
  CustomFlowbiteTheme,
  Dropdown,
  DropdownProps as FlowbiteDropdownProps,
  Flowbite,
} from "flowbite-react";
import { FC, ReactNode } from "react";

interface DropDownItemProps {
  icon?: ReactNode;
  label?: string;
  onClick?: () => void;
}

interface DropdownProps extends FlowbiteDropdownProps {
  label: string;
  items: DropDownItemProps[];
  icon?: ReactNode;
}

const Dropdowntheme: CustomFlowbiteTheme = {
  button: {
    color: {
      transparent: `bg-transparent`,
    },
    inner: {
      base: "flex items-center text-sm w-full ",
    },
    size: {
      md: "flex items-center justify-between gap-1 px-1 text-xs md:text-sm  py-0 w-full",
      xs: "px-3 py-1.5 md:text-sm text-xs",
    },
  },
  dropdown: {
    arrowIcon: "ml-2 h-4 w-4 hidden  darK:hidden",
    content:
      "py-0 focus:outline-none  border border-purple-200 dark:border-gray-700 rounded-lg dark:bg-gray-900 -mt-5 ",
    floating: {
      animation: "transition-opacity",
      base: "z-10 w-fit divide-y divide-gray-100 rounded shadow focus:outline-none rounded-lg ml-2 -mt-2 dark:bg-gray-900 ",
      content: "py-1 text-sm text-gray-700 dark:text-gray-200 ",
      divider: "my-1 h-px bg-gray-100 dark:bg-gray-600",
      header: "block px-0 py-2 text-sm text-gray-700 dark:text-gray-200",
      hidden: "invisible opacity-0",
      item: {
        container: "rounded-lg  h-full ",
        base: "flex w-full rounded-lg cursor-pointer items-center justify-start px-3 pr-8 h-[40px]  text-sm text-gray-700 bg-white dark:bg-black hover:bg-Slate-200  focus:outline-none dark:text-gray-200 group-dark:hover:bg-gray-880 dark:hover:text-white  ",
        icon: "mr-2 h-5 w-5",
      },
      style: {
        dark: "bg-gray-900 text-white dark:bg-gray-700",
        light: "border border-red-500 bg-white text-gray-900",
        auto: "border border-gray-200 bg-white text-gray-900 dark:border-none dark:bg-gray-700 dark:text-white",
      },
      target: "w-fit",
    },
    inlineWrapper: "flex items-center",
  },
};
const Dropdown_: FC<DropdownProps> = ({ icon, items, label, ...props }) => {
  return (
    <Flowbite theme={{ theme: Dropdowntheme }}>
      <Dropdown
        {...props}
        color="transparent"
        label={
          <span className="flex items-center gap-[12px] font-[600] p-[4px] lg:py-5 text-lg text-black dark:text-white">
            {icon}
            <span className="hidden lg:flex text-[14px]">{label}</span>
          </span>
        }
      >
        {items?.map((item: any, index: number) => (
          <Dropdown.Item key={index} onClick={item.onClick}>
            {item.icon}
            <span className=" whitespace-nowrap">{item.label}</span>
          </Dropdown.Item>
        ))}
      </Dropdown>
    </Flowbite>
  );
};

export default Dropdown_;
