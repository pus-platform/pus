const convertUrlsToLinks = (text: string) => {
  const urlRegex = /(https?:\/\/[^\s]+)/g;

  return text?.replace(urlRegex, (url) => {
    return `<a href="${url}" target="_blank" rel="noopener noreferrer" class="text-blue-600 font-semibold">${url}</a>`;
  });
};

const HashtagText = ({ text }) => {
  const linkedText = convertUrlsToLinks(text);

  return (
    <div
      className="md:text-xl text-lg dark:text-white"
      dangerouslySetInnerHTML={{ __html: linkedText }}
    />
  );
};

export default HashtagText;
