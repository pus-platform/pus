import React, { useState, useEffect, useRef } from "react";
import Heading from "../../components/Heading/Heading";
import { Pencil } from 'lucide-react';
import Textarea from "../../components/common/Textarea/Textarea";
import Button from "../../components/common/Button/Button";
import { useNavigate, useParams } from "react-router-dom";
import { SlDocs } from "react-icons/sl";
import { MdOutlineOndemandVideo } from "react-icons/md";
import { MdOutlinePhotoLibrary } from "react-icons/md";
import Select from "../../components/common/Select/Select";
import { Post } from "@/lib/types";
import { useGetPostById, useUpdatePost } from "@/lib/react-query/queries/post";
import { View } from "@/lib/enums";
import Loader from "@/components/common/Loader/Loader";
import UploadFileInput from "@/components/UploadFileInput/UploadFileInput";

const EditPost: React.FC = () => {
  const { id } = useParams();
  const { data: post, isPending: isPostLoading } = useGetPostById({ id: parseInt(id) });
  const { mutateAsync: updatePost } = useUpdatePost({ id: parseInt(id) });
  const navigate = useNavigate();
  const [formData, setFormData] = useState<Post>({
    imageUrl: "",
    caption: "",
    view: View.PUBLIC,
  });
  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (post && id)
      setFormData(post);
  }, [id, post]);

  if (isPostLoading) return <Loader />

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (files) {
      setFormData({
        ...formData,
        imageUrl: formData.imageUrl,
      });
    }
  };

  const handleRemoveImage = () => {
    setFormData({
      ...formData,
      imageUrl: "",
    });
  };

  const getFileExtension = (filename: string): string => {
    return filename.split(".").pop()?.toLowerCase() || "";
  };

  const getFileIcon = (extension: string) => {
    if (
      extension === "png" ||
      extension === "jpeg" ||
      extension === "jpg" ||
      extension === "svg"
    ) {
      //TODO: icon
      return <MdOutlinePhotoLibrary className="w-10 h-10 text-purple-500" />;
    } else if (
      extension === "mp4" ||
      extension === "avi" ||
      extension === "mkv" ||
      extension === "mov" ||
      extension === "webm" ||
      extension === "ogg"
    ) {
      //TODO: icon
      return <MdOutlineOndemandVideo className="w-10 h-10 text-purple-500" />;
    } else if (extension === "pdf" || extension === "docx") {
      //TODO: icon
      return <SlDocs className="w-10 h-10 text-purple-500" />;
    } else {
      return null;
    }
  };

  const handleSubmit = () => {
    updatePost(formData);
    navigate("/");
  };

  return (
    <div className="flex flex-col items-center w-screen h-full dark:text-white">
      <div className="flex flex-col align-center w-full flex-grow px-4 py-6 md:px-10 lg:px-20">
        <Heading icon={<Pencil className="h-8 w-8" />} label="Edit Post" />
        <div className="flex space-x-6 items-center"></div>
        <Textarea
          label="caption"
          placeholder="Caption"
          name="caption"
          value={formData.caption}
          onChange={handleChange}
        />
      </div>

      <Select
        label="Privacy"
        name="view"
        className="flex w-full items-stretch px-4 py-6 md:px-10 lg:px-20"
        options={[
          { name: "Public", value: View.PUBLIC },
          { name: "Private", value: View.PRIVATE },
          { name: "Community", value: View.COMMUNITY },
        ]}
        value={formData.view}
        onChange={handleChange}
      />
      <Button
        label="Save Changes"
        size="sm"
        color="purpleFit"
        onClick={handleSubmit}
      />
    </div>
  );
};

export default EditPost;
