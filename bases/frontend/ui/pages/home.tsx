import React from "react";
import { useState, useEffect } from "react";
import { useLocation } from "wouter";
import Footer from "../components/landing/Footer";
import { useToast } from "../hooks/use-toast";

export default function Home(props) {
  const [location, navigate] = useLocation();
  const { toast } = useToast();

  const isLoggedIn = props.isLoggedIn;
  const user = props.user;
  const activeComponent = props.activeComponent;

  const surveyComponent = React.Children.toArray(props.children).find(
    child => child.props['id'] === 'survey'
  );

  const waitingListComponent = React.Children.toArray(props.children).find(
    child => child.props['id'] === 'waiting-list'
  );
  
  // This handler will be called when the survey is completed
  const onSurveyCompleted = (email: string) => {
    // Navigate to waiting list with email parameter
    navigate(`/waiting-list?email=${encodeURIComponent(email)}`);
    
    toast({
      title: "Survey completed!",
      description: "Thank you for your feedback. You've been added to our waiting list.",
    });
  };

  if (isLoggedIn) {
    return null; // Will redirect to dashboard
  }
  
  return (
    <div className="min-h-screen bg-gradient-to-b from-white via-slate-50 to-white">
      <main className="pt-8 pb-16">
        {activeComponent === "survey" && (
          surveyComponent
        )}

        {activeComponent === "waiting-list" && (
          waitingListComponent
        )}
      </main>
      
      <Footer />
    </div>
  );
}
