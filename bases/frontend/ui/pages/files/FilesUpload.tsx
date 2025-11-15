import React from "react";
import { useRef } from 'react';
import { useState } from "react";
import { useToast } from "../../hooks/use-toast";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "../../components/ui/card";
import { Button } from "../../components/ui/button";
import { Input } from "../../components/ui/input";
import { Label } from "../../components/ui/label";
import { 
  UploadCloud, 
  FileSpreadsheet, 
  FileText,
  AlertTriangle, 
  Loader2,
  Mail,
  FileIcon
} from "lucide-react";
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "../../components/ui/accordion";
import { Badge } from "../../components/ui/badge";

type FileData = {
  id: number;
  userId: number;
  filename: string;
  fileType: string;
  uploadDate: string;
  processed: boolean;
  extractedData: any;
};

export default function FilesUpload(props) {
  const { toast } = useToast();
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [selectedFileData, setSelectedFileData] = useState<FileData | null>(null);
  
  const isLoading = props.isLoading;

  const fileInputRef = useRef(null);

  const handleButtonClick = (e) => {
    e.preventDefault(); // prevent form submission if button is in a form
    fileInputRef.current?.click();
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setSelectedFile(e.target.files[0]);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!selectedFile) {
      toast({
        title: "No file selected",
        description: "Please select a file to upload",
        variant: "destructive",
      });
      return;
    }

    // Check file type
    const fileExt = selectedFile.name.split('.').pop()?.toLowerCase();
    const allowedTypes = ['xlsx', 'xls'];
    
    if (!fileExt || !allowedTypes.includes(fileExt)) {
      toast({
        title: "Invalid file type",
        description: "Please upload an Excel",
        variant: "destructive",
      });
      return;
    }
    
    // Hint to user about the file being processed
    if (['xlsx', 'xls'].includes(fileExt)) {
      toast({
        title: "Tenants data processing",
        description: "We'll attempt to import the tenants data automatically.",
      });
    }

    const formData = new FormData();
    formData.append('file', selectedFile);
  };

  // Function to get file icon based on file type
  const getFileIcon = (fileType: string) => {
    switch(fileType.toLowerCase()) {
      case 'xlsx':
      case 'xls':
      case 'csv':
        return <FileSpreadsheet className="h-5 w-5 mr-2 text-green-600" />;
      case 'pdf':
        return <FileIcon className="h-5 w-5 mr-2 text-red-600" />;
      case 'doc':
      case 'docx':
        return <FileText className="h-5 w-5 mr-2 text-blue-600" />;
      default:
        return <FileText className="h-5 w-5 mr-2 text-gray-600" />;
    }
  };

  const currentYear = new Date().getFullYear();
  const years = Array.from({ length: 3 }, (_, i) => currentYear - i);
  const selectedYear = props.year;

  const renderExtractedData = (file: FileData) => {
    
    // Render other structured data
    return (
      <div className="mt-4">
        <h4 className="font-medium mb-2">Extracted Data:</h4>
        <Accordion type="single" collapsible className="w-full">
          {Object.entries(file.extractedData).map(([key, value], index) => (
            <AccordionItem key={index} value={`item-${index}`}>
              <AccordionTrigger className="text-sm font-medium">
                {key.charAt(0).toUpperCase() + key.slice(1).replace(/([A-Z])/g, ' $1')}
              </AccordionTrigger>
              <AccordionContent>
                <pre className="bg-gray-50 p-2 rounded-md text-xs overflow-x-auto">
                  {JSON.stringify(value, null, 2)}
                </pre>
              </AccordionContent>
            </AccordionItem>
          ))}
        </Accordion>
      </div>
    );
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>Dateil hochladen</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="bg-blue-50 border border-blue-200 rounded-md p-4 mb-6">
          <h4 className="font-medium text-blue-800">Statusanalyse</h4>
          <p className="text-sm text-blue-700 mt-1">
            Anhand einer Ihrer Excel-Dateien werden wir untersuchen, wie wir Ihnen all die damit verbundenen Probleme abnehmen können.
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* ROW: Email + Upload Button */}
          <div className="flex items-center justify-between w-full max-w-lg">

            {/* EMAIL FIELD */}
            <div className="flex flex-col w-1/2">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                placeholder="you@example.com"
                className="mt-1"
                onChange={(e) => props.onEmailChange?.(e.target.value)}
              />
            </div>

            {/* UPLOAD BUTTON */}
            <div>
              <Input
                id="file-upload"
                type="file"
                className="hidden"
                accept=".xlsx,.xls"
                onChange={props.onUploadData}
                ref={fileInputRef}
                disabled={false}
              />

              <Button
                type="button"
                onClick={handleButtonClick}
                variant="outline"
                size="sm"
              >
                <UploadCloud className="mr-2 h-4 w-4" />
                Upload Document
              </Button>
            </div>

          </div>
        </form>  
      </CardContent>
    </Card>
  );
}
