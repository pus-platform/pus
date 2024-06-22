import { HiDocumentDuplicate } from "react-icons/hi";

interface CardProps {
  imgSrc: { url: string; name: string };
  icon?: boolean;
}

const Card: React.FC<CardProps> = ({ imgSrc, icon }) => {
  const { name, url } = imgSrc;

  const getFileExtension = (filename: string): string => {
    return filename.split(".").pop()?.toLowerCase() || "";
  };

  const fileExtension = getFileExtension(name);

  let mediaContent: JSX.Element | null = null;

  if (
    fileExtension === "png" ||
    fileExtension === "jpeg" ||
    fileExtension === "jpg" ||
    fileExtension === "svg"
  ) {
    mediaContent = (
      <img src={url} alt={name} className="w-full h-full object-cover  block" />
    );
  } else if (
    fileExtension === "mp4" ||
    fileExtension === "avi" ||
    fileExtension === "mkv" ||
    fileExtension === "mov" ||
    fileExtension === "webm" ||
    fileExtension === "ogg"
  ) {
    mediaContent = (
      <video
        src={url}
        controls
        className="w-full h-full object-contain block"
      />
    );
  } else if (fileExtension === "pdf" || fileExtension === "docx") {
    mediaContent = (
      <iframe
        src={url + "#toolbar=0"}
        title={name}
        className="w-full h-full"
        frameBorder="0"
      />
    );
  }

  return (
    <div className={`group relative top-shadow md:rounded-[30px] rounded-lg w-full md:max-w-[542px] block md:h-[350px] h-[300px] overflow-hidden`} >
      {icon && (
        <HiDocumentDuplicate className="absolute right-6 top-6 h-6 w-6 group-hover:text-purple-500 transition dark:text-white" />
      )}
      {mediaContent}
    </div>
  );
};

export default Card;
