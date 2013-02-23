\documentclass[a4paper,12pt]{article}
\usepackage[landscape]{geometry}
\usepackage[utf8x]{inputenc}
\usepackage{multirow}
\usepackage{color}
\usepackage[usenames,dvipsnames,table]{xcolor}
\usepackage{float}

\begin{document}

\begin{center}
  {\huge \textbf{AI-MAS Winter Olympics: Crafting Quest 3}} \\
  {\huge Semi-final Round Report}
\end{center}


\section{Interpreting Results}
\label{sec:overview}

Five games between all four teams were played. 
The teams received 3, 2, 1 or 0 points, depending on their final score in each game. The team with the highest score received 3 points, the second 2, and so on.If two or more players got the same number of points at the end of one game, they received the number of points corresponding to the highest place. 

In each game, the starting positions were randomly chosen. The maps were alternated in a circular manner (map 1, map 2, map 3, map 1, map2).


Section \ref{sec:general} shows
you the general placement.  Section \ref{sec:all} gives you the detailed results of each
match outcome.

\section{Semifinals Overview}
\label{sec:general}

\begin{table}[H]
  \centering
  \caption{Semi-finals Overview}
  \begin{tabular}{|c|c|c|c|c|c|}
    \hline
      \textbf{Team Name} & \textbf{1} & \textbf{2} & \textbf{3} & \textbf{4} & \textbf{Score} \\
      \hline
      \hline
      {% for teamitem in teamscores %} 
            {{teamitem.ename}} & {{ teamitem.p1 }} & {{ teamitem.p2 }} & {{ teamitem.p3 }} & {{ teamitem.p4 }} & {{ teamitem.total_points }} \\
            \hline  
                {% endfor %}
                
              \end{tabular}
              \label{table:overview}
          \end{table}

\section{Individual Match Results}
          \label{sec:all}

\begin{minipage}[b]{0.45\linewidth}
  Definitions:\\\vspace*{.5em}
  \begin{tabular}[b]{l l}
    \textbf{K}  & Kills \\
    \textbf{RK} & Retaliation Kills \\
    \textbf{DU} & Dead Units \\
    \textbf{Ptower} & Placed Towers \\
    \textbf{Strap} & Succesful Traps \\
    \textbf{Ptrap} & Placed Traps \\
    \textbf{KS} & Killing Spree \\
    \textbf{FB} & First Blood
  \end{tabular}
\end{minipage}
\hspace{0.5cm}
\begin{minipage}[b]{0.45\linewidth}
  Starting positions:\\\vspace*{.5em}
  \begin{tabular}[b] {l l}
    \textbf{ULC} & Upper Left Corner \\
    \textbf{URC} & Upper Right Corner \\
    \textbf{LLC} & Lower Left Corner \\
    \textbf{LRC} & Lower Right Corner 
  \end{tabular}
  \vspace*{1em} Results:\\\vspace*{.5em}
  \begin{tabular}[b]{l l}
    \textbf{L} & Lose \\
    \textbf{D} & Draw \\
    \textbf{W} & Win
  \end{tabular}
\end{minipage}

{% for m in gamescores %}

\subsection{ {{ gamescores[m].ename }} }
           {% for g in gamescores[m].games %}
  \vspace*{2em}
  \begin{tabular}[t]{| c | c | c | c | c | c | c | c | c | c | c | c
      |}
    \hline
    Starting Position & \textbf{Points} & Player & \textbf{Score} & K & RK & DU & PTower & STrap & PTrap & KS & FB \\
    {% for p in gamescores[m].games[g] %}
        \hline
            {% if gamescores[m].games[g][p].player_id == 1 %} ULC 
              {% elif gamescores[m].games[g][p].player_id == 2 %} LRC
              {% elif gamescores[m].games[g][p].player_id == 3 %} URC 
                {% else %} LLC {% endif %} &
                
                {{ gamescores[m].games[g][p].points }} & 
                {{ gamescores[m].games[g][p].ename }} & 
                {{ gamescores[m].games[g][p].total_score }} &
                {{ gamescores[m].games[g][p].kills }} &
                {{ gamescores[m].games[g][p].retaliation_kills }} &
                {{ gamescores[m].games[g][p].dead_units }} &
                {{ gamescores[m].games[g][p].placed_towers }} &
                {{ gamescores[m].games[g][p].successful_traps}} & 
                {{ gamescores[m].games[g][p].placed_traps }} & 
                {{ gamescores[m].games[g][p].killing_sprees }} &
                {{ gamescores[m].games[g][p].first_blood }} \\
                {% endfor %}
                  \hline
  \end{tabular}
  {% endfor %}
\newpage
{% endfor %}
                    

\end{document}
