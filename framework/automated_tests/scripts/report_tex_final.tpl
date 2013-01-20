\documentclass[a4paper,10pt]{article}
\usepackage[landscape]{geometry}
\usepackage[utf8x]{inputenc}
\usepackage{multirow}
\begin{document}
\par {\huge \textbf{AI-MAS Winter Olympics: Crafting Quest 2}} \\
{\large Team \textit {{thisteamname}} Report} \\

\label{sec:results}
\section{Interpreting Results}

\par Section \ref{sec:overview} presents a general overview of the results and your team's position on the board. The score in Table \ref{table:overview} is computed by the following rules:
\begin{itemize}
	\item Each win is worth 2 points. A game is won by the team obtaining the highest score.
	\item Each draw is worth 1 point. A game is considered a draw if both teams obtain the same score.
	\item Each lost game is worth 0 points, the loosing team being the one obtaining the lowest score.
\end{itemize}

\par Table \ref{table:average} shows the average battle score.

\label{sec:overview}
\section{Final Round Overview (2011.02.12)}

\begin{table}[ht]
\begin{minipage}[b]{0.5\linewidth}\centering
\caption{Final Round Overview}
\begin{tabular}{|c|c|c|c|c|}
\hline
\textbf{Team No.} & \textbf{W} & \textbf{D} & \textbf{L} & \textbf{Score} \\
\hline
\hline
{% for teamitem in teamscores %} 
        {{ teamitem.name }} & {{ teamitem.w }} & {{ teamitem.d }} & {{ teamitem.l }} & {{ teamitem.s }} \\
        \hline  
{% endfor %}

\end{tabular}
\label{table:overview}
\end{minipage}
\hspace{0.5cm}
\begin{minipage}[b]{0.5\linewidth}\centering
\caption{Final Round Average Scores}
\label{table:average}
\begin{tabular}{|c|c|}
\hline
\textbf{Team No.} & \textbf{Average Score} \\
\hline
\hline
{% for teamitem in teamscores %}
        {{ teamitem.name }} & {{ teamitem.averagescore }} \\
        \hline  
{% endfor %}

\end{tabular}
\end{minipage}
\end{table}


\newpage

\section{Results per map}

{% for mapname in mapnames %}
\label{sec:map{{forloop.counter}}.cqm}

\begin{table}[!htb]
\caption{Results for map{{forloop.counter}}.cqm}
\centering

\begin{tabular}{|c||{% for teamitem in teamscores %}c|{% endfor %} }
\hline
{% for teamitem in teamscores %} & {{teamitem.name}} {% endfor %} \\
\hline
\hline

{% for teamitem in teamscores %} {{teamitem.name}}  
    {% for mname, gamelist in teamitem.games.items %}
    	{% ifequal mname mapname %} {% for game in gamelist %} {% if game %} & {{ game.me }} / {{ game.op }} {% else %} & -- {% endif %} {% endfor %} \\ {% endifequal %} 
    {% endfor %}
    \hline
{% endfor %} 

\end{tabular}
\label{table:map{{forloop.counter}}.cqm}
\end{table}

{% endfor %}

\end{document}
