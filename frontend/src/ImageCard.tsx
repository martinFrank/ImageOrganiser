import React, { useEffect, useState } from 'react';

const ImageCard: React.FC = () => {
  const [imageUrl, setImageUrl] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchImage = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/images/island');
        if (!response.ok) {
          throw new Error('Failed to fetch image');
        }
        const blob = await response.blob();
        const url = URL.createObjectURL(blob);
        setImageUrl(url);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Unknown error');
      } finally {
        setLoading(false);
      }
    };

    fetchImage();
  }, []);

  if (loading) {
    return (
      <div className="image-card">
        <p>Loading image...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="image-card">
        <p>Error: {error}</p>
      </div>
    );
  }

  return (
    <div className="image-card">
      <img src={imageUrl!} alt="Island Image" />
    </div>
  );
};

export default ImageCard;