import React, { useState } from "react";
import Heading from "../../components/Heading/Heading";
import { SquarePlus } from "lucide-react";
import { View } from "@/lib/enums";
import UploadFileInput from "../../components/UploadFileInput/UploadFileInput";
import Textarea from "../../components/common/Textarea/Textarea";
import Button from "../../components/common/Button/Button";
import { useNavigate } from "react-router-dom";
import { SlDocs } from "react-icons/sl";
import { MdOutlineOndemandVideo } from "react-icons/md";
import { MdOutlinePhotoLibrary } from "react-icons/md";
import Select from "../../components/common/Select/Select";
import { useCreatePost } from "@/lib/react-query/queries/post";
import { Post } from "@/lib/types";

interface PostData {
  id: string;
  userId: string;
  imgSrc: any;
  photoOrVideoAltText: string;
  location: string;
  description: string;
  likes: number;
}

const CreatePost = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState<Post>({
    imageUrl: "",
    caption: "",
  });
  const [imageUrl, setImageUrl] = useState("");
  const [images, setImages] = useState([]);
  const [error, setError] = useState("");

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(e.target.files);
    if (files) {
      setFormData({
        ...formData,
        imageUrl: formData.imageUrl,
      });
      setImageUrl(files[0].name);
    }
    const fileURLs = files.map((file) => URL.createObjectURL(file));
    const updatedImages = files.map((file, index) => ({
      url: fileURLs[index],
      file,
      name: file.name,
    }));
    setImages([...images, ...updatedImages]);
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
      return <MdOutlinePhotoLibrary className="w-10 h-10 text-purple-500" />;
    } else if (
      extension === "mp4" ||
      extension === "avi" ||
      extension === "mkv" ||
      extension === "mov" ||
      extension === "webm" ||
      extension === "ogg"
    ) {
      return <MdOutlineOndemandVideo className="w-10 h-10 text-purple-500" />;
    } else if (extension === "pdf" || extension === "docx") {
      return <SlDocs className="w-10 h-10 text-purple-500" />;
    } else {
      return null;
    }
  };
  const { mutateAsync: createPostMutation } = useCreatePost();
  const addPost = (newPost: Post) => {
    createPostMutation(newPost)
  };
  const handleSubmit = async () => {
    if (!formData.caption.trim()) {
      setError("Description is required");
      return;
    }
    // @ts-ignore
    const newPost: PostData = {
      id: Date.now().toString(),
      imgSrc: images.map((image) => image),
      description: formData.caption,
      location: formData.location,
      photoOrVideoAltText: formData.photoOrVideoAltText,
      likes: 0,
    };
    // @ts-ignore
    addPost(newPost);
    // createPostMutation(newPost).then(() => {
    navigate("/");
    setFormData({
      caption: "",
      imageUrl: "",
      view: View.PUBLIC,
    });
    setImageUrl("");
    setImages([]);
    setError("");
    // });
  };

  return (
    <div className="dark:text-white h-full w-full px-2 md:px-8 pb-40">
      <Heading
        icon={<SquarePlus className="h-8 w-8" />}
        label="Create a Post"
      />
      <div className="flex flex-col align-center flex-grow px-4 py-6 md:px-10 lg:px-20 overflow-y-scroll">
        <div className="relative w-full">
          <Textarea
            value={formData.caption}
            placeholder="Caption (required)"
            name="caption"
            onChange={handleChange}
          />
          {error && <p className="text-red-500 text-sm my-1">{error}</p>}
        </div>
        <div className="relative flex flex-wrap w-full space-x-4 items-center gap-4 rounded-lg my-1">
          {getFileIcon(getFileExtension(imageUrl))}
          <p>{imageUrl}</p>
          <button
            className="absolute -top-2 text-xs -right-4 bg-white p-1 px-2 text-red-500 shadow-sm   rounded-full"
            onClick={() => handleRemoveImage()}
          >
            X
          </button>
        </div>
        <div className="self-center w-full">
          <UploadFileInput
            caption={`Photo/Video`}
            onChange={handleFileChange}
          />
        </div>
        <Select
          label="Select a Visibility"
          name="view"
          options={[
            { name: "Public", value: View.PUBLIC },
            { name: "Private", value: View.PRIVATE },
            { name: "Community", value: View.COMMUNITY },
          ]}
          value={formData.view}
          onChange={handleChange}
        />
        <div className="ml-auto my-6">
          <Button
            label="Create Post"
            size="sm"
            color="purpleFit"
            onClick={handleSubmit}
          />
        </div>
      </div>
    </div>
  );
};

export default CreatePost;
