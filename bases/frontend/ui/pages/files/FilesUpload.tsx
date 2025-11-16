import React, { useRef, useState } from "react";
import { useToast } from "../../hooks/use-toast";
import { Card, CardContent, CardHeader, CardTitle } from "../../components/ui/card";
import { Button } from "../../components/ui/button";
import { Input } from "../../components/ui/input";
import { UploadCloud, FileSpreadsheet, FileIcon, FileText } from "lucide-react";

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
      toast({ title: "Keine Datei ausgewählt", description: "Bitte wählen Sie eine Excel-Datei aus.", variant: "destructive" });
      return;
    }

    const fileExt = selectedFile.name.split('.').pop()?.toLowerCase();
    const allowedTypes = ['xlsx', 'xls'];

    if (!fileExt || !allowedTypes.includes(fileExt)) {
      toast({ title: "Ungültiger Dateityp", description: "Bitte laden Sie eine Excel-Datei hoch.", variant: "destructive" });
      return;
    }

    // Hint to user about the file being processed
    if (['xlsx', 'xls'].includes(fileExt)) {
      toast({ title: "Datei wird verarbeitet", description: "Wir analysieren Ihre Excel-Datei und erstellen Ihr Mietprofil." });
    }

    const formData = new FormData();
    formData.append('file', selectedFile);
  };

  return (
    <div className="space-y-8">
      {/* HERO SECTION */}
      <section className="max-w-5xl mx-auto text-center pb-8 px-4">
        <h1 className="text-4xl md:text-5xl font-bold text-gray-900 leading-tight">
          Vermieten ohne Excel – wir übernehmen Ihre komplette Mietverwaltung
        </h1>

        <p className="text-xl text-gray-700 mt-6 leading-relaxed max-w-3xl mx-auto">
          Viele private Vermieter in Deutschland verwalten ihre Immobilien noch manuell mit Excel. 
          Wir analysieren Ihre Datei, stellen Ihnen gezielte Fragen per E-Mail und übernehmen die gesamte Verwaltung für Sie. 
          Kein Chaos, keine Doppelarbeit – einfach starten.
        </p>

        <ul className="mt-10 space-y-3 text-gray-800 text-lg max-w-xl mx-auto text-left">
          <li className="flex gap-3"><span>✔️</span> Nie wieder Excel-Chaos oder manuelle Pflege</li>
          <li className="flex gap-3"><span>✔️</span> Vollständige Ordnung in Miete, Nebenkosten & Belegen</li>
          <li className="flex gap-3"><span>✔️</span> Persönliche Rückfragen per E-Mail – bequem beantwortbar</li>
          <li className="flex gap-3"><span>✔️</span> Wir erstellen automatisch Ihr digitales Mietprofil</li>
          <li className="flex gap-3"><span>✔️</span> Null Verwaltungsaufwand für Sie</li>
        </ul>

        <p className="text-xl text-gray-900 font-semibold mt-10">
          Starten Sie jetzt mit Ihrer kostenlosen Analyse.
        </p>

        <button
          onClick={() => {
            const uploadCard = document.getElementById("upload-section");
            if (uploadCard) uploadCard.scrollIntoView({ behavior: "smooth" });
          }}
          className="mt-6 px-8 py-3 bg-blue-600 text-white text-lg font-medium rounded-xl hover:bg-blue-700 transition"
        >
          Jetzt starten – Excel hochladen
        </button>
      </section>

      {/* UPLOAD SECTION */}
      <section id="upload-section" className="max-w-3xl mx-auto px-4 py-48">
        <Card>
          <CardHeader>
            <CardTitle>Datei hochladen & Analyse starten</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="bg-blue-50 border border-blue-200 rounded-md p-4 mb-6">
              <h4 className="font-medium text-blue-800">Wie funktioniert es?</h4>
              <p className="text-sm text-blue-700 mt-1">
                Laden Sie einfach Ihre bestehende Excel-Datei hoch. Wir analysieren alle Daten und stellen Ihnen gezielte Fragen per E-Mail, um Ihr Mietprofil zu erstellen – komplett automatisch. Sie müssen sich um nichts kümmern.
              </p>
            </div>

            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="flex items-center w-full gap-3">
                <Input
                  id="email"
                  type="email"
                  placeholder="Ihre E-Mail-Adresse"
                  className="flex-grow"
                  required
                  onChange={(e) => props.onEmailChange?.(e.target.value)}
                />

                <div>
                  <Input
                    id="file-upload"
                    type="file"
                    className="hidden"
                    accept=".xlsx,.xls"
                    onChange={props.onUploadData}
                    ref={fileInputRef}
                  />
                  <Button
                    type="button"
                    onClick={handleButtonClick}
                    variant="outline"
                    size="sm"
                    className="flex items-center"
                  >
                    <UploadCloud className="mr-2 h-4 w-4" />
                    Datei auswählen
                  </Button>
                </div>
              </div>

              <Button type="submit" className="w-full bg-blue-600 text-white hover:bg-blue-700 transition">
                Analyse starten
              </Button>
            </form>
          </CardContent>
        </Card>
      </section>
    </div>
  );

};