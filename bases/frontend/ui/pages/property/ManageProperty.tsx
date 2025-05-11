import React from "react";
import { useRef } from 'react';
import { useState } from "react";
import { Link } from 'wouter';
import { queryClient } from "../../lib/queryClient";
import { useToast } from "../../hooks/use-toast";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "../../components/ui/card";
import { Button } from "../../components/ui/button";
import { Input } from "../../components/ui/input";
import { Label } from "../../components/ui/label";
import { 
  UploadCloud, 
  FileSpreadsheet, 
  FileText, 
  Check, 
  AlertTriangle, 
  Loader2, 
  Eye, 
  Clock,
  CircleX,
  FileIcon
} from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "../../components/ui/dialog";
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

export default function ManageProperty(props) {
  const { toast } = useToast();
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [selectedFileData, setSelectedFileData] = useState<FileData | null>(null);
  
  const data = null;
  const files = null;
  const isLoading = props.isLoading;
  const transactions = props.transactions;

  const fileInputRef = useRef(null);

  const handleButtonClick = (e) => {
    e.preventDefault(); // prevent form submission if button is in a form
    console.log("Test")
  };

  // Fetch previously uploaded files
  /*const { data: files, isLoading } = useQuery({
    queryKey: ['/api/files'],
    queryFn: () => fetch('/api/files').then(res => res.json())
  });*/

  // Setup file upload mutation
  /*const uploadMutation = useMutation({
    mutationFn: (formData: FormData) => {
      return fetch('/api/upload', {
        method: 'POST',
        body: formData,
        credentials: 'include'
      }).then(res => {
        if (!res.ok) throw new Error('Upload failed');
        return res.json();
      });
    },
    onSuccess: () => {
      setSelectedFile(null);
      queryClient.invalidateQueries({ queryKey: ['/api/files'] });
      toast({
        title: "File uploaded successfully",
        description: "Your file is being processed with AI-powered data extraction. This may take a minute.",
      });
    },
    onError: (error: Error) => {
      toast({
        title: "Upload failed",
        description: error.message,
        variant: "destructive",
      });
    }
  });*/

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
    const allowedTypes = ['xlsx', 'xls', 'csv', 'pdf', 'doc', 'docx', 'txt'];
    
    if (!fileExt || !allowedTypes.includes(fileExt)) {
      toast({
        title: "Invalid file type",
        description: "Please upload a document in one of these formats: Excel, CSV, PDF, Word, or text",
        variant: "destructive",
      });
      return;
    }
    
    // Hint to user about the file being processed
    if (['xlsx', 'xls', 'csv'].includes(fileExt)) {
      toast({
        title: "Bank Statement Processing",
        description: "If this is a bank statement, we'll attempt to import the transactions into your accounting system automatically.",
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

  // Function to render extracted data or error
  const renderExtractedData = (file: FileData) => {
    if (!file.extractedData) return null;
    
    // Check if there was an error during processing
    if (file.extractedData.processingFailed || file.extractedData.error) {
      return (
        <div className="bg-red-50 p-4 rounded-md mt-4">
          <div className="flex items-start">
            <CircleX className="h-5 w-5 text-red-500 mr-2 mt-0.5" />
            <div>
              <h4 className="font-medium text-red-800">Processing Error</h4>
              <p className="text-sm text-red-700 mt-1">
                {file.extractedData.error || "Failed to extract data from this file"}
              </p>
            </div>
          </div>
        </div>
      );
    }

    // Check if we have raw text instead of structured data
    if (file.extractedData.rawText) {
      return (
        <div className="mt-4">
          <h4 className="font-medium mb-2">Extracted Text:</h4>
          <div className="bg-gray-50 p-3 rounded-md text-sm whitespace-pre-wrap max-h-60 overflow-y-auto">
            {file.extractedData.rawText}
          </div>
        </div>
      );
    }

    // Check if it's a bank statement with transactions
    if (file.extractedData.document_type === 'bank_statement' && Array.isArray(file.extractedData.transactions)) {
      const transactions = file.extractedData.transactions;
      
      // Calculate summary statistics
      const incomeTotal = transactions
        .filter((t: any) => t.type === 'income')
        .reduce((sum: number, t: any) => sum + (parseFloat(t.amount) || 0), 0);
      
      const expenseTotal = transactions
        .filter((t: any) => t.type === 'expense')
        .reduce((sum: number, t: any) => sum + (parseFloat(t.amount) || 0), 0);
      
      const netAmount = incomeTotal - expenseTotal;
      
      // Count transactions by category
      const categoryCounts: Record<string, { count: number, amount: number, type: string }> = {};
      transactions.forEach((t: any) => {
        const category = t.category || 'Uncategorized';
        if (!categoryCounts[category]) {
          categoryCounts[category] = { count: 0, amount: 0, type: t.type };
        }
        categoryCounts[category].count += 1;
        categoryCounts[category].amount += parseFloat(t.amount) || 0;
      });
      
      const topCategories = Object.entries(categoryCounts)
        .sort(([, a], [, b]) => b.amount - a.amount)
        .slice(0, 5);
        
      return (
        <div className="mt-4 space-y-6">
          <div className="bg-green-50 p-4 rounded-md mb-4 border border-green-200">
            <div className="flex items-start">
              <Check className="h-5 w-5 text-green-500 mr-2 mt-0.5" />
              <div>
                <h4 className="font-medium text-green-800">Bank Statement Processed</h4>
                <p className="text-sm text-green-700 mt-1">
                  {transactions.length} transactions were extracted and added to your accounting system.
                </p>
              </div>
            </div>
          </div>
          
          {/* Bank statement details */}
          <div>
            <h4 className="font-medium mb-2">Bank Statement Details:</h4>
            <div className="grid grid-cols-2 gap-4 mb-4 text-sm bg-gray-50 p-4 rounded-md">
              <div>
                <p><span className="font-medium">Bank Name:</span> {file.extractedData.bank_name || 'Unknown'}</p>
                <p><span className="font-medium">Account Number:</span> {file.extractedData.account_number || 'Unknown'}</p>
              </div>
              <div>
                <p><span className="font-medium">Statement Period:</span> {file.extractedData.statement_period?.start_date ? (
                  `${file.extractedData.statement_period.start_date} to ${file.extractedData.statement_period.end_date || 'present'}`
                ) : 'Not specified'}</p>
              </div>
            </div>
          </div>
          
          {/* Summary statistics */}
          <div>
            <h4 className="font-medium mb-3">Summary</h4>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="bg-blue-50 p-4 rounded-md border border-blue-100">
                <h4 className="text-sm font-medium text-blue-800">Total Transactions</h4>
                <p className="text-2xl font-semibold mt-1">{transactions.length}</p>
                <p className="text-xs text-blue-600 mt-1">From {file.filename}</p>
              </div>
              
              <div className="bg-green-50 p-4 rounded-md border border-green-100">
                <h4 className="text-sm font-medium text-green-800">Total Income</h4>
                <p className="text-2xl font-semibold text-green-700 mt-1">€{incomeTotal.toFixed(2)}</p>
                <p className="text-xs text-green-600 mt-1">
                  {transactions.filter((t: any) => t.type === 'income').length} income transactions
                </p>
              </div>
              
              <div className="bg-red-50 p-4 rounded-md border border-red-100">
                <h4 className="text-sm font-medium text-red-800">Total Expenses</h4>
                <p className="text-2xl font-semibold text-red-700 mt-1">€{expenseTotal.toFixed(2)}</p>
                <p className="text-xs text-red-600 mt-1">
                  {transactions.filter((t: any) => t.type === 'expense').length} expense transactions
                </p>
              </div>
            </div>
          </div>
          
          {/* Net position */}
          <div className="p-4 rounded-md border" 
               style={{ 
                 backgroundColor: netAmount >= 0 ? 'rgba(0, 128, 0, 0.05)' : 'rgba(255, 0, 0, 0.05)',
                 borderColor: netAmount >= 0 ? 'rgba(0, 128, 0, 0.2)' : 'rgba(255, 0, 0, 0.2)'
               }}>
            <h4 className="text-sm font-medium" 
                style={{ color: netAmount >= 0 ? 'rgb(0, 100, 0)' : 'rgb(180, 0, 0)' }}>
              Net Position
            </h4>
            <p className="text-3xl font-bold mt-1" 
               style={{ color: netAmount >= 0 ? 'rgb(0, 128, 0)' : 'rgb(220, 0, 0)' }}>
              {netAmount >= 0 ? '+' : ''}€{netAmount.toFixed(2)}
            </p>
            <p className="text-xs mt-1" 
               style={{ color: netAmount >= 0 ? 'rgb(0, 100, 0)' : 'rgb(180, 0, 0)' }}>
              {netAmount >= 0 ? 'Positive balance' : 'Negative balance'} for this statement period
            </p>
          </div>
          
          {/* Top categories */}
          <div>
            <h4 className="font-medium mb-3">Top Categories</h4>
            <div className="space-y-2">
              {topCategories.map(([category, data]) => (
                <div key={category} className="flex items-center justify-between p-3 bg-gray-50 rounded-md">
                  <div className="flex items-center">
                    <div className="w-3 h-3 rounded-full mr-2" 
                        style={{ backgroundColor: data.type === 'income' ? 'rgb(0, 150, 0)' : 'rgb(220, 50, 50)' }}></div>
                    <span>{category}</span>
                  </div>
                  <div className="flex items-center">
                    <span className="text-sm mr-2">{data.count} transactions</span>
                    <span className={`font-medium ${data.type === 'income' ? 'text-green-600' : 'text-red-600'}`}>
                      {data.type === 'income' ? '+' : '-'}€{data.amount.toFixed(2)}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
          
          <h4 className="font-medium mb-2">All Transactions:</h4>
          <div className="overflow-x-auto border rounded-md">
            <table className="min-w-full divide-y divide-gray-200 text-sm">
              <thead className="bg-gray-50">
                <tr>
                  <th scope="col" className="px-4 py-2 text-left font-medium text-gray-500">Date</th>
                  <th scope="col" className="px-4 py-2 text-left font-medium text-gray-500">Description</th>
                  <th scope="col" className="px-4 py-2 text-left font-medium text-gray-500">Amount</th>
                  <th scope="col" className="px-4 py-2 text-left font-medium text-gray-500">Type</th>
                  <th scope="col" className="px-4 py-2 text-left font-medium text-gray-500">Category</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {transactions.map((transaction: any, index: number) => (
                  <tr key={index} className={index % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                    <td className="px-4 py-2 whitespace-nowrap">{transaction.date}</td>
                    <td className="px-4 py-2">{transaction.description}</td>
                    <td className={`px-4 py-2 whitespace-nowrap font-medium ${transaction.type === 'income' ? 'text-green-600' : 'text-red-600'}`}>
                      {transaction.type === 'income' ? '+' : '-'}€{Math.abs(parseFloat(transaction.amount)).toFixed(2)}
                    </td>
                    <td className="px-4 py-2 whitespace-nowrap">
                      <Badge variant={transaction.type === 'income' ? 'outline' : 'secondary'}>
                        {transaction.type}
                      </Badge>
                    </td>
                    <td className="px-4 py-2 whitespace-nowrap">{transaction.category || 'Uncategorized'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          
          <div className="mt-4 p-4 bg-green-50 border border-green-200 rounded-md">
            <h4 className="font-medium text-green-800 flex items-center">
              <Check className="h-5 w-5 text-green-500 mr-2" />
              Transactions Successfully Imported
            </h4>
            <p className="text-sm text-green-700 mt-1">
              All transactions have been automatically imported into your accounting system. 
              You can review and edit them in the Transactions section.
            </p>
          </div>
        </div>
      );
    }
    
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
        <CardTitle>Property - {props.selectedProperty}</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="mt-8">
          <h3 className="text-lg font-medium mb-4">Manage property data</h3>
          
          <div className="space-y-3">
            <div key="electricity" className="transition-colors hover:bg-gray-100 rounded p-2">
              <div className="flex items-center justify-between w-full">
                <div className="flex w-full">
                  <p className="font-medium">Allgemeinstrom</p>
                </div>

                {props.editElectricity ? (
                  <Input
                    className="w-[100px] h-8 text-right text-sm"
                    placeholder="Used electricity"
                    defaultValue={props.electricity}
                    onBlur={props.onChangePropertyElectricity}
                  />
                ) : (
                  <a
                    onClick={props.onEditElectricity}
                    className="text-blue-600 underline cursor-pointer whitespace-nowrap h-8 flex items-center text-sm"
                  >
                    {props.electricity ? `€ ${props.electricity}` : 'Add'}
                  </a>
                )}  
              </div>
            </div>

            <div key="accountability" className="transition-colors hover:bg-gray-100 rounded p-2">
              <div className="flex items-center justify-between w-full">
                <div className="flex w-full">
                  <p className="font-medium">Gebäude/Haftpflicht</p>
                </div>

                {props.editAccountability ? (
                  <Input
                    className="w-[100px] h-8 text-right text-sm"
                    placeholder="Insurance paid"
                    defaultValue={props.accountability}
                    onBlur={props.onChangePropertyAccountability}
                  />
                ) : (
                  <a
                    onClick={props.onEditAccountability}
                    className="text-blue-600 underline cursor-pointer whitespace-nowrap h-8 flex items-center text-sm"
                  >
                    {props.accountability ? `€ ${props.accountability}` : 'Add'}
                  </a>
                )}
                
              </div>
            </div>

            <div key="property-tax" className="transition-colors hover:bg-gray-100 rounded p-2">
              <div className="flex items-center justify-between w-full">
                <div className="flex w-full">
                  <p className="font-medium">Grundsteuer</p>
                </div>

                {props.editTax ? (
                  <Input
                    className="w-[100px] h-8 text-right text-sm"
                    placeholder="Insurance paid"
                    defaultValue={props.tax}
                    onBlur={props.onChangePropertyTax}
                  />
                ) : (
                  <a
                    onClick={props.onEditTax}
                    className="text-blue-600 underline cursor-pointer whitespace-nowrap h-8 flex items-center text-sm"
                  >
                    {props.tax ? `€ ${props.tax}` : 'Add'}
                  </a>
                )}  
              </div>
            </div>

            <div key="garbage" className="transition-colors hover:bg-gray-100 rounded p-2">
              <div className="flex items-center justify-between w-full">
                <div className="flex w-full">
                  <p className="font-medium">Müllabfuhr</p>
                </div>

                {props.editGarbage ? (
                  <Input
                    className="w-[100px] h-8 text-right text-sm"
                    placeholder="Garbate aervice paid"
                    defaultValue={props.garbage}
                    onBlur={props.onChangePropertyGarbage}
                  />
                ) : (
                  <a
                    onClick={props.onEditGarbage}
                    className="text-blue-600 underline cursor-pointer whitespace-nowrap h-8 flex items-center text-sm"
                  >
                    {props.garbage ? `€ ${props.garbage}` : 'Add'}
                  </a>
                )}  
              </div>
            </div>

            <div key="rain-water" className="transition-colors hover:bg-gray-100 rounded p-2">
              <div className="flex items-center justify-between w-full">
                <div className="flex w-full">
                  <p className="font-medium">Niederschlagwasser</p>
                </div>

                {props.editRainwater ? (
                  <Input
                    className="w-[100px] h-8 text-right text-sm"
                    placeholder="Rain water paid"
                    defaultValue={props.rainwater}
                    onBlur={props.onChangePropertyRainwater}
                  />
                ) : (
                  <a
                    onClick={props.onEditRainwater}
                    className="text-blue-600 underline cursor-pointer whitespace-nowrap h-8 flex items-center text-sm"
                  >
                    {props.rainwater ? `€ ${props.rainwater}` : 'Add'}
                  </a>
                )}  
              </div>
            </div>
          </div>
        </div>
      </CardContent>

      <div className="p-4 border-t flex gap-2">
        <Button
          variant="outline"
          className="flex items-center justify-center"
          onClick={props.onSaveSelection}
        >
          Continue
        </Button>

        <Button
          variant="outline"
          className="flex items-center justify-center"
          onClick={props.onCancel}
        >
          Cancel
        </Button>
      </div>
    </Card>
  );
}
